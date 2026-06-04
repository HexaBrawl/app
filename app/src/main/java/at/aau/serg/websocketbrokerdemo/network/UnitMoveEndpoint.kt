package at.aau.serg.websocketbrokerdemo.network

import android.util.Log
import at.aau.serg.websocketbrokerdemo.data.serverside.ErrorMessage
import at.aau.serg.websocketbrokerdemo.data.serverside.GameState
import at.aau.serg.websocketbrokerdemo.data.serverside.Move
import at.aau.serg.websocketbrokerdemo.data.serverside.PlayerColor
import com.google.gson.Gson
import kotlinx.coroutines.Job

/**
 * Wrapper um die STOMP-Topics rund ums Spiel.
 *
 * Bietet drei Methoden, eine pro logische Aktion: Game-State
 * abonnieren, Errors abonnieren, Move senden. Plus Join: hier kommt
 * jetzt auch die gewuenschte Spieler-Farbe mit, damit der Server sie
 * dem `Player`-Modell zuweisen kann.
 *
 * Bridge-Verhalten:
 *  Aktuell schickt der Server bei [joinGame] noch keine Color-Antwort
 *  zurueck (`Player.color` bleibt also auf dem Server-Default).
 *  Sobald der Server den Endpoint unterstuetzt, kommt die Farbe ohne
 *  weitere Frontend-Aenderung im naechsten GameState-Update mit.
 */
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

    /**
     * Tritt einem Spiel bei und uebergibt Name + gewuenschte Farbe.
     *
     * Wird als JSON gesendet, damit der Server die Felder typisiert
     * lesen kann (im Gegensatz zum frueher genutzten plain-text-Format
     * mit nur dem Namen). Das Backend muss den Endpoint entsprechend
     * akzeptieren.
     */
    fun joinGame(roomId: String, playerName: String, color: PlayerColor) {
        val payload = JoinRequest(name = playerName, color = color.name)
        val json = gson.toJson(payload)
        Log.d(TAG, "-> /app/rooms/$roomId/join: $json")
        stomp.sendJson("/app/rooms/$roomId/join", json)
    }

    /**
     * DTO fuer den Join-Request. Wird hier als interne data class
     * gehalten, damit der Server-Vertrag in einer Stelle definiert ist.
     */
    private data class JoinRequest(
        val name: String,
        val color: String
    )

    companion object {
        private const val TAG = "UnitMoveEndpoint"
    }
}
