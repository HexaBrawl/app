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
 * Bietet drei "normale" Aktionen (Game-State abonnieren, Errors
 * abonnieren, Move senden) plus zwei Cheat-Gift-Endpoints:
 *  - [claimCheatGift]    Spieler hat das Geschenk-Icon 5x geklickt
 *                        und sendet das gewuerfelte Delta an den Server
 *  - [respondToCheatGift] Spieler reagiert auf das Steal-Popup
 *                        (accept=true zum Stehlen, false zum Ablehnen)
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

    fun joinGame(roomId: String, playerName: String, color: PlayerColor) {
        val payload = JoinRequest(name = playerName, color = color.name)
        val json = gson.toJson(payload)
        Log.d(TAG, "-> /app/rooms/$roomId/join: $json")
        stomp.sendJson("/app/rooms/$roomId/join", json)
    }

    /**
     * Schummel-Geschenk oeffnen: Frontend hat bereits gewuerfelt und
     * schickt das Delta an den Server. Server addiert/subtrahiert das
     * Delta vom Spieler-Gold und setzt [GameState.pendingGift], sodass
     * alle anderen Spieler das Steal-Popup sehen koennen.
     *
     * Server-Validierung: nur erlaubt wenn Player.hasUsedGift == false
     * und GameState.pendingGift == null.
     */
    fun claimCheatGift(roomId: String, playerName: String, delta: Int) {
        val payload = ClaimGiftRequest(playerName = playerName, delta = delta)
        val json = gson.toJson(payload)
        Log.d(TAG, "-> /app/rooms/$roomId/cheat/claim-gift: $json")
        stomp.sendJson("/app/rooms/$roomId/cheat/claim-gift", json)
    }

    /**
     * Antwort auf das Steal-Popup. [accept] = true bedeutet "Ja klauen";
     * = false bedeutet "Nein, lass ihm das Gold". Server lehnt ab wenn
     * der Spieler der Owner ist oder wenn pendingGift bereits weg ist
     * (jemand war schneller).
     */
    fun respondToCheatGift(roomId: String, playerName: String, accept: Boolean) {
        val payload = StealResponseRequest(playerName = playerName, accept = accept)
        val json = gson.toJson(payload)
        Log.d(TAG, "-> /app/rooms/$roomId/cheat/respond-steal: $json")
        stomp.sendJson("/app/rooms/$roomId/cheat/respond-steal", json)
    }

    private data class JoinRequest(
        val name: String,
        val color: String
    )

    private data class ClaimGiftRequest(
        val playerName: String,
        val delta: Int
    )

    private data class StealResponseRequest(
        val playerName: String,
        val accept: Boolean
    )

    companion object {
        private const val TAG = "UnitMoveEndpoint"
    }
}
