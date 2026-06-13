package at.aau.serg.websocketbrokerdemo.network

import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.config.HeartBeat
import org.hildan.krossbow.stomp.frame.FrameBody
import org.hildan.krossbow.stomp.headers.StompSendHeaders
import org.hildan.krossbow.stomp.subscribeText
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient
import kotlin.time.Duration.Companion.seconds

/**
 * Single STOMP entry-point for the whole app.
 *
 * Wichtige Eigenschaften:
 *  - connect() ist mit Retry abgesichert (Azure Cold-Start kann den ersten
 *    Versuch in einen Timeout laufen lassen).
 *  - connect() ist Mutex-geschuetzt, damit parallele Aufrufe sich nicht
 *    in die Quere kommen.
 *  - subscribe() / sendText() / sendJson() warten automatisch ueber
 *    awaitConnected(), bis STOMP wirklich verbunden ist. Damit gibt es
 *    keine Race-Condition mehr zwischen App-Start und erster Lobby-Aktion:
 *    der User kann sofort einen Raum erstellen oder beitreten, die Frames
 *    werden gepuffert/verzoegert bis der WebSocket steht.
 *  - HeartBeat 20s/20s ist im StompClient konfiguriert (matched den
 *    Server). Verhindert Idle-Timeout-Drops (Azure schliesst die WS
 *    sonst nach ~120s ohne Traffic).
 *  - Bei einem unerwarteten Disconnect (Display aus, Netzwechsel, Cell
 *    Tower Wechsel, ...) faehrt [Stomp] automatisch eine Reconnect-Loop
 *    (max [MAX_RECONNECT_ATTEMPTS] Versuche im [RECONNECT_DELAY_MS]
 *    Abstand). Status ist ueber [connectionState] als StateFlow
 *    beobachtbar; Subscriber koennen sich ueber [onReconnected] eine
 *    Benachrichtigung holen, sobald die Verbindung wieder steht
 *    (typischer Use-Case: dann den /reconnect-Application-Call senden).
 *
 * NOTE on content-type:
 *  - sendText  -> "text/plain"        (for /app/join)
 *  - sendJson  -> "application/json"  (for /app/move - Spring deserializes to DTO)
 * The default `sendText` extension of Krossbow would send everything as text/plain,
 * which prevents Spring's Jackson converter from mapping the payload to a `Move` DTO.
 */
class Stomp(
    private val websocketUri: String = DEFAULT_WEBSOCKET_URI
) {
    private var client: StompClient? = null
    private var session: StompSession? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val connectMutex = Mutex()

    /**
     * True sobald [disconnect] aufgerufen wurde. Verhindert dass die
     * Auto-Reconnect-Loop nach einem absichtlichen Disconnect (App-Shutdown)
     * weiter versucht zu verbinden.
     */
    @Volatile
    private var intentionallyDisconnected = false

    private val _connectionState = MutableStateFlow(ConnectionState.Connected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private var reconnectJob: Job? = null
    private val onReconnectedCallbacks = mutableListOf<() -> Unit>()
    private val callbacksLock = Any()

    val isConnected: Boolean get() = session != null

    /**
     * Registriert einen Callback, der jedes Mal feuert, wenn die
     * Auto-Reconnect-Loop einen STOMP-Wiederaufbau geschafft hat.
     * Wird typischerweise vom GameViewModel genutzt, um danach den
     * /reconnect-Application-Call zu senden.
     */
    fun onReconnected(callback: () -> Unit) {
        synchronized(callbacksLock) { onReconnectedCallbacks += callback }
    }

    /**
     * Verbindet zum STOMP-Broker, mit automatischem Retry um Azure-Cold-Start
     * abzufedern. Bei einem frisch gestarteten App-Backend dauert der erste
     * TLS-Handshake regelmaessig laenger als der OkHttp-Default-Timeout (10s);
     * deshalb bis zu [MAX_CONNECT_ATTEMPTS]-mal probieren, mit einer kurzen
     * Pause zwischen den Versuchen.
     *
     * Ist Mutex-geschuetzt, sodass parallele Aufrufer sich nicht doppelt
     * verbinden und stattdessen auf den laufenden Connect warten.
     *
     * Wirft die letzte Exception erst, wenn alle Versuche fehlgeschlagen
     * sind. Solange ein vorhandener Connect erfolgreich war (session != null),
     * ist der Aufruf ein No-Op.
     */
    suspend fun connect() {
        if (session != null) return
        connectMutex.withLock {
            if (session != null) return
            var lastError: Throwable? = null
            for (attempt in 1..MAX_CONNECT_ATTEMPTS) {
                try {
                    doConnectOnce()
                    Log.i(TAG, "Connected to $websocketUri (attempt $attempt)")
                    _connectionState.value = ConnectionState.Connected
                    return
                } catch (e: Exception) {
                    lastError = e
                    Log.w(TAG, "STOMP connect attempt $attempt/$MAX_CONNECT_ATTEMPTS failed: ${e.message}")
                    if (attempt < MAX_CONNECT_ATTEMPTS) {
                        delay(RETRY_DELAY_MS)
                    }
                }
            }
            throw lastError ?: IllegalStateException("STOMP connect failed without error")
        }
    }

    /**
     * Wartet, bis die STOMP-Verbindung steht. Bei bereits stehender
     * Verbindung sofortige Rueckkehr; sonst wird (Mutex-geschuetzt) ein
     * Connect-Versuch ausgeloest und auf dessen Ergebnis gewartet.
     *
     * Die Send-/Subscribe-Methoden dieser Klasse rufen das intern auf,
     * damit Aufrufer sich nicht selbst um Timing kuemmern muessen.
     */
    suspend fun awaitConnected() = connect()

    /**
     * Abonniert ein STOMP-Topic. Wartet automatisch, bis STOMP verbunden
     * ist (kein stiller No-Op mehr wie frueher). Wenn der Subscribe-Flow
     * endet (Server-Disconnect, Netzwerk weg, ...), wird der
     * Auto-Reconnect getriggert.
     */
    fun subscribe(
        topic: String,
        onMessage: (String) -> Unit
    ): Job = scope.launch {
        try {
            awaitConnected()
            val s = session
            if (s == null) {
                Log.e(TAG, "subscribe($topic) aborted: session still null after awaitConnected")
                return@launch
            }
            s.subscribeText(topic).collect { onMessage(it) }
            // Subscribe-Flow normal beendet -> Session ist down.
            Log.w(TAG, "Subscription to $topic ended (session closed)")
            handleSessionLost("subscribe $topic ended")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Subscription to $topic failed", e)
            handleSessionLost("subscribe $topic threw")
        }
    }

    /**
     * Sendet einen rohen Text-Payload (content-type: text/plain).
     * Wartet automatisch, bis STOMP verbunden ist.
     */
    fun sendText(destination: String, payload: String) {
        scope.launch {
            try {
                awaitConnected()
                val s = session
                if (s == null) {
                    Log.e(TAG, "sendText($destination) aborted: session still null after awaitConnected")
                    return@launch
                }
                s.send(
                    StompSendHeaders(destination) { contentType = "text/plain" },
                    FrameBody.Text(payload)
                )
            } catch (e: Exception) {
                Log.e(TAG, "sendText to $destination failed", e)
            }
        }
    }

    /**
     * Sendet einen JSON-Payload (content-type: application/json), damit Spring
     * den Body direkt in ein DTO deserialisieren kann.
     * Wartet automatisch, bis STOMP verbunden ist.
     */
    fun sendJson(destination: String, json: String) {
        scope.launch {
            try {
                awaitConnected()
                val s = session
                if (s == null) {
                    Log.e(TAG, "sendJson($destination) aborted: session still null after awaitConnected")
                    return@launch
                }
                s.send(
                    StompSendHeaders(destination) { contentType = "application/json" },
                    FrameBody.Text(json)
                )
            } catch (e: Exception) {
                Log.e(TAG, "sendJson to $destination failed", e)
            }
        }
    }

    fun disconnect() {
        intentionallyDisconnected = true
        scope.launch {
            try {
                session?.disconnect()
            } catch (e: Exception) {
                Log.w(TAG, "disconnect failed", e)
            } finally {
                session = null
                client = null
            }
        }
    }

    // ---- Internals ------------------------------------------------------

    /** Einzelner Connect-Versuch mit HeartBeat-Config. */
    private suspend fun doConnectOnce() {
        val c = StompClient(OkHttpWebSocketClient()) {
            heartBeat = HeartBeat(
                minSendPeriod = HEARTBEAT_PERIOD,
                expectedPeriod = HEARTBEAT_PERIOD,
            )
        }
        client = c
        session = c.connect(websocketUri)
    }

    /**
     * Markiert die aktuelle Session als verloren und startet die
     * Auto-Reconnect-Loop. Idempotent: wenn schon ein Reconnect laeuft
     * oder der App-User absichtlich disconnected hat, passiert nichts.
     */
    private fun handleSessionLost(reason: String) {
        if (intentionallyDisconnected) return
        synchronized(callbacksLock) {
            if (reconnectJob?.isActive == true) return
            session = null
            client = null
            _connectionState.value = ConnectionState.Reconnecting
            Log.w(TAG, "Session lost ($reason) -- starting reconnect loop")
            reconnectJob = scope.launch {
                val success = ReconnectLogic.retryUntilSuccess(
                    maxAttempts = MAX_RECONNECT_ATTEMPTS,
                    delayMillis = RECONNECT_DELAY_MS,
                    attempt = { tryReconnectOnce() }
                )
                if (success) {
                    Log.i(TAG, "Reconnect successful")
                    _connectionState.value = ConnectionState.Connected
                    fireReconnectedCallbacks()
                } else {
                    Log.e(TAG, "Reconnect failed after $MAX_RECONNECT_ATTEMPTS attempts")
                    _connectionState.value = ConnectionState.LostPermanently
                }
            }
        }
    }

    private suspend fun tryReconnectOnce(): Boolean {
        return try {
            connectMutex.withLock {
                if (session != null) return@withLock true
                doConnectOnce()
                true
            }
        } catch (e: Exception) {
            Log.w(TAG, "Reconnect attempt failed: ${e.message}")
            false
        }
    }

    private fun fireReconnectedCallbacks() {
        val snapshot = synchronized(callbacksLock) { onReconnectedCallbacks.toList() }
        snapshot.forEach { cb ->
            try {
                cb()
            } catch (e: Exception) {
                Log.e(TAG, "onReconnected callback threw", e)
            }
        }
    }

    companion object {
        private const val TAG = "Stomp"

        /**
         * Maximale Anzahl an Connect-Versuchen bevor der finale Fehler
         * weitergeworfen wird.
         */
        private const val MAX_CONNECT_ATTEMPTS = 3

        /**
         * Pause zwischen zwei Connect-Versuchen in Millisekunden.
         */
        private const val RETRY_DELAY_MS = 2000L

        /**
         * Maximale Anzahl an Auto-Reconnect-Versuchen nach einem
         * unerwarteten Disconnect. 15 * 2s = 30s und matched die Grace-
         * Period des Servers (Spieler wird nach 30s aus dem Raum entfernt).
         */
        private const val MAX_RECONNECT_ATTEMPTS = 15

        /**
         * Pause zwischen zwei Auto-Reconnect-Versuchen in Millisekunden.
         */
        private const val RECONNECT_DELAY_MS = 2000L

        /**
         * STOMP-Heartbeat-Periode. 20s matched die Server-Konfiguration;
         * zwei Heartbeats pro Minute halten die WebSocket im Azure-Idle-
         * Timeout (120s) sicher offen.
         */
        private val HEARTBEAT_PERIOD = 20.seconds

        /**
         * Production backend on Azure. Use `wss://` (TLS) - the matching health endpoint is
         * https://hexabrawl-server-fkcdhrf9avdbebh5.germanywestcentral-01.azurewebsites.net/health
         */
        const val DEFAULT_WEBSOCKET_URI =
            "wss://hexabrawl-server-fkcdhrf9avdbebh5.germanywestcentral-01.azurewebsites.net/websocket-example-broker"
    }
}
