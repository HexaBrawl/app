package at.aau.serg.websocketbrokerdemo.network

import android.util.Log
import at.aau.serg.websocketbrokerdemo.data.serverside.ErrorMessage
import at.aau.serg.websocketbrokerdemo.data.serverside.GameState
import at.aau.serg.websocketbrokerdemo.data.serverside.Move
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Workaround toggle for the deployed (Azure) backend.
 *
 * `WebSocketBrokerController.move()` on the server checks
 * `stateAfter === stateBefore` to detect rejected moves, but both references
 * point to the same singleton `GameState`, so the check is always true. The
 * controller therefore never broadcasts the result of a successful move.
 *
 * Until the server is redeployed with the fix, the client re-fetches the state
 * via `/app/init` shortly after every move so the UI catches up.
 *
 * Set this to false once the fixed server is in production.
 */
private const val AZURE_MOVE_BROADCAST_WORKAROUND = true
private const val POST_MOVE_REFRESH_DELAY_MS = 150L

class UnitMoveEndpoint(
    private val stomp: Stomp
) {

    private val gson = Gson()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun subscribeToGameState(onGameState: (GameState) -> Unit): Job =
        stomp.subscribe("/topic/game") { msg ->
            Log.d(TAG, "<- /topic/game: $msg")
            try {
                onGameState(gson.fromJson(msg, GameState::class.java))
            } catch (e: Exception) {
                Log.e(TAG, "Failed parsing GameState: $msg", e)
            }
        }

    fun subscribeToErrors(onError: (ErrorMessage) -> Unit): Job =
        stomp.subscribe("/user/queue/errors") { msg ->
            Log.w(TAG, "<- /user/queue/errors: $msg")
            try {
                onError(gson.fromJson(msg, ErrorMessage::class.java))
            } catch (e: Exception) {
                Log.e(TAG, "Failed parsing ErrorMessage: $msg", e)
            }
        }

    fun sendMove(move: Move) {
        try {
            val json = gson.toJson(move)
            Log.d(TAG, "-> /app/move: $json")
            stomp.sendJson("/app/move", json)

            if (AZURE_MOVE_BROADCAST_WORKAROUND) {
                scope.launch {
                    delay(POST_MOVE_REFRESH_DELAY_MS)
                    requestInitialState()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed sending move", e)
        }
    }

    fun joinGame(playerName: String) {
        Log.d(TAG, "-> /app/join: $playerName")
        stomp.sendText("/app/join", playerName)
    }

    fun requestInitialState() {
        Log.d(TAG, "-> /app/init")
        stomp.sendText("/app/init", "")
    }

    companion object {
        private const val TAG = "UnitMoveEndpoint"
    }
}
