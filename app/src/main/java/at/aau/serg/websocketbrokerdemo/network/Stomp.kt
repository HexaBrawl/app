package at.aau.serg.websocketbrokerdemo.network

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.sendText
import org.hildan.krossbow.stomp.subscribeText
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient

private const val WEBSOCKET_URI = "ws://10.0.2.2:8080/websocket-example-broker"

/*
* New Stomp class to enable a more generalized use and limited complexity.
* Endpoint classes can register here which allows for a more untangled network logic.
* */
class Stomp {
    private lateinit var client: StompClient
    private var session: StompSession? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    suspend fun connect() {
        client = StompClient(OkHttpWebSocketClient())
        session = client.connect(WEBSOCKET_URI)
    }

    fun subscribe(
        topic: String,
        onMessage: (String) -> Unit
    ): Job {
        return scope.launch {
            session?.subscribeText(topic)?.collect {
                onMessage(it)
            }
        }
    }

    fun send(destination: String, payload: String) {
        scope.launch {
            session?.sendText(destination, payload)
        }
    }

    fun disconnect() {
        scope.launch {
            session?.disconnect()
        }
    }
}