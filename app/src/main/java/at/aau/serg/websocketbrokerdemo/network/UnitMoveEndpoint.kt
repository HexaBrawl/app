package at.aau.serg.websocketbrokerdemo.network

import android.util.Log
import at.aau.serg.websocketbrokerdemo.data.serverside.ErrorMessage
import at.aau.serg.websocketbrokerdemo.data.serverside.GameState
import at.aau.serg.websocketbrokerdemo.data.serverside.Move
import at.aau.serg.websocketbrokerdemo.data.serverside.PlayerColor
import at.aau.serg.websocketbrokerdemo.data.serverside.UnitType
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

    /**
     * Tritt einem Raum bei.
     *
     * Farb-Vergabe: Wenn [color] null ist, vergibt der Server eine freie
     * Farbe und schickt sie via GameState-Broadcast zurueck. Damit gibt
     * es keine Race-Condition zwischen "beide Clients schicken default
     * RED" und "Server lehnt den zweiten mit COLOR_ALREADY_TAKEN ab".
     *
     * Eine explizite Farbe wird nur dann mitgegeben, wenn der User in
     * der Wartelobby bewusst eine bestimmte Farbe gewaehlt hat. Solange
     * das UI noch keinen Color-Picker hat, bleibt color = null.
     */
    fun joinGame(roomId: String, playerName: String, color: PlayerColor? = null) {
        val payload = JoinRequest(name = playerName, color = color?.name)
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

    /**
     * Kauft eine Farm fuer den Spieler. Server zieht das Gold ab und
     * erhoeht das Einkommen (income) entsprechend. Validierung
     * server-seitig (isMyTurn, genug Gold, etc.).
     */
    fun buyFarm(roomId: String, playerName: String) {
        val payload = BuyFarmRequest(playerName = playerName)
        val json = gson.toJson(payload)
        Log.d(TAG, "-> /app/rooms/$roomId/buy-farm: $json")
        stomp.sendJson("/app/rooms/$roomId/buy-farm", json)
    }

    /**
     * Kauft eine Einheit und platziert sie an Position (x, y). Server
     * validiert ob die Zelle dem Spieler gehoert und ob genug Gold da
     * ist.
     */
    fun buyUnit(roomId: String, playerName: String, type: UnitType, x: Int, y: Int) {
        val payload = BuyUnitRequest(
            playerName = playerName,
            type = type.name,
            x = x,
            y = y
        )
        val json = gson.toJson(payload)
        Log.d(TAG, "-> /app/rooms/$roomId/buy-unit: $json")
        stomp.sendJson("/app/rooms/$roomId/buy-unit", json)
    }

    /**
     * Beendet die Runde des lokalen Spielers. Server rotiert den
     * currentTurn auf den naechsten Spieler und schreibt das Einkommen
     * (income) dem Spieler gut.
     */
    fun endTurn(roomId: String, playerName: String) {
        val payload = EndTurnRequest(playerName = playerName)
        val json = gson.toJson(payload)
        Log.d(TAG, "-> /app/rooms/$roomId/end-turn: $json")
        stomp.sendJson("/app/rooms/$roomId/end-turn", json)
    }

    private data class JoinRequest(val name: String, val color: String?)
    private data class ClaimGiftRequest(val playerName: String, val delta: Int)
    private data class StealResponseRequest(val playerName: String, val accept: Boolean)
    private data class BuyFarmRequest(val playerName: String)
    private data class BuyUnitRequest(val playerName: String, val type: String, val x: Int, val y: Int)
    private data class EndTurnRequest(val playerName: String)

    companion object { private const val TAG = "UnitMoveEndpoint" }
}
