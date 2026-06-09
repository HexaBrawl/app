package at.aau.serg.websocketbrokerdemo.network

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.frame.FrameBody
import org.hildan.krossbow.stomp.headers.StompSendHeaders
import org.hildan.krossbow.stomp.subscribeText
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient

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

    val isConnected: Boolean get() = session != null

    /**
     * Verbindet zum STOMP-Broker, mit automatischem Retry um Azure-Cold-Start
     * abzufedern. Bei einem frisch gestarteten App-Backend dauert der erste
     * TLS-Handshake regelmaessig laenger als der OkHttp-Default-Timeout (10s);
     * deshalb bis zu MAX_CONNECT_ATTEMPTS-mal probieren, mit einer kurzen
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
                    val c = StompClient(OkHttpWebSocketClient())
                    client = c
                    session = c.connect(websocketUri)
                    Log.i(TAG, "Connected to $websocketUri (attempt $attempt)")
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
     * ist (kein stiller No-Op mehr wie frueher).
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
        } catch (e: Exception) {
            Log.e(TAG, "Subscription to $topic failed", e)
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
         * Production backend on Azure. Use `wss://` (TLS) - the matching health endpoint is
         * https://hexabrawl-server-fkcdhrf9avdbebh5.germanywestcentral-01.azurewebsites.net/health
         */
        const val DEFAULT_WEBSOCKET_URI =
            "wss://hexabrawl-server-fkcdhrf9avdbebh5.germanywestcentral-01.azurewebsites.net/websocket-example-broker"
    }
}
