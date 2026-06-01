package at.aau.serg.websocketbrokerdemo.network

import android.util.Log
import at.aau.serg.websocketbrokerdemo.data.serverside.ErrorMessage
import at.aau.serg.websocketbrokerdemo.data.serverside.GameState
import at.aau.serg.websocketbrokerdemo.data.serverside.Move
import com.google.gson.Gson
import kotlinx.coroutines.Job

class UnitMoveEndpoint(
    private val stomp: Stomp
) {

    private val gson = Gson()

    fun subscribeToGameState(roomId: String, onGameState: (GameState) -> Unit): Job =
        stomp.subscribe("/topic/rooms/$roomId/state") { msg ->
            Log.d(TAG, "<- /topic/rooms/$roomId/state: $msg")
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

    fun sendMove(roomId: String, move: Move) {
        try {
            val json = gson.toJson(move)
            Log.d(TAG, "-> /app/rooms/$roomId/move: $json")
            stomp.sendJson("/app/rooms/$roomId/move", json)
        } catch (e: Exception) {
            Log.e(TAG, "Failed sending move", e)
        }
    }

    fun joinGame(roomId: String, playerName: String) {
        Log.d(TAG, "-> /app/rooms/$roomId/join: $playerName")
        stomp.sendText("/app/rooms/$roomId/join", playerName)
    }

    fun requestInitialState(roomId: String) {
        Log.d(TAG, "-> /app/rooms/$roomId/init/")
        stomp.sendText("/app/rooms/$roomId/init/", "")
    }

    companion object {
        private const val TAG = "UnitMoveEndpoint"
    }
}
