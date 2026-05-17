package at.aau.serg.websocketbrokerdemo.network

import android.util.Log
import at.aau.serg.websocketbrokerdemo.Stomp
import at.aau.serg.websocketbrokerdemo.data.serverside.GameState
import at.aau.serg.websocketbrokerdemo.data.serverside.Move
import com.google.gson.Gson

class UnitMoveEndpoint(
    private val stomp: Stomp
) {

    private val gson = Gson()

    fun subscribeToGameState(
        onGameState: (GameState) -> Unit
    ) {
        stomp.subscribe("/topic/game") { msg ->
            try {
                val state = gson.fromJson(msg, GameState::class.java)
                onGameState(state)
            } catch (e: Exception) {
                Log.e("UnitMoveEndpoint", "Failed parsing GameState", e)
            }
        }
    }

    fun sendMove(move: Move) {
        try {
            val json = gson.toJson(move)

            stomp.send(
                "/app/move",
                json
            )
        } catch (e: Exception) {
            Log.e("UnitMoveEndpoint", "Failed sending move", e)
        }
    }

    fun joinGame(playerName: String) {
        stomp.send(
            "/app/join",
            playerName
        )
    }

    fun requestInitialState() {
        stomp.send(
            "/app/init",
            ""
        )
    }
}