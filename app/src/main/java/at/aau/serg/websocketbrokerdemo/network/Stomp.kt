package at.aau.serg.websocketbrokerdemo.network

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.frame.FrameBody
import org.hildan.krossbow.stomp.headers.StompSendHeaders
import org.hildan.krossbow.stomp.subscribeText
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient

/**
 * Single STOMP entry-point for the whole app.
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

    val isConnected: Boolean get() = session != null

    suspend fun connect() {
        if (session != null) return
        val c = StompClient(OkHttpWebSocketClient())
        client = c
        session = c.connect(websocketUri)
        Log.i(TAG, "Connected to $websocketUri")
    }

    fun subscribe(
        topic: String,
        onMessage: (String) -> Unit
    ): Job = scope.launch {
        try {
            session?.subscribeText(topic)?.collect { onMessage(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Subscription to $topic failed", e)
        }
    }

    /** Sends a raw text payload (content-type: text/plain). */
    fun sendText(destination: String, payload: String) {
        scope.launch {
            try {
                session?.send(
                    StompSendHeaders(destination) { contentType = "text/plain" },
                    FrameBody.Text(payload)
                )
            } catch (e: Exception) {
                Log.e(TAG, "sendText to $destination failed", e)
            }
        }
    }

    /** Sends a JSON payload (content-type: application/json) so Spring can deserialize to a DTO. */
    fun sendJson(destination: String, json: String) {
        scope.launch {
            try {
                session?.send(
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
         * Production backend on Azure. Use `wss://` (TLS) - the matching health endpoint is
         * https://hexabrawl-server-fkcdhrf9avdbebh5.germanywestcentral-01.azurewebsites.net/health
         */
        const val DEFAULT_WEBSOCKET_URI =
            "wss://hexabrawl-server-fkcdhrf9avdbebh5.germanywestcentral-01.azurewebsites.net/websocket-example-broker"
    }
}
