package at.aau.serg.websocketbrokerdemo.ui.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import at.aau.serg.websocketbrokerdemo.network.ConnectionState
import at.aau.serg.websocketbrokerdemo.network.GameSession
import kotlinx.coroutines.delay

/**
 * Resync nach (Re-)Connect.
 *
 * STOMP-Subscriptions liefern nur ZUKUENFTIGE Broadcasts -- waehrend
 * einer Trennung verpasste Aenderungen (Zugwechsel, pendingGift, ...)
 * fehlen sonst, der Client haengt oder zeigt kein Popup.
 *
 * Sobald die Verbindung steht und ein Match laeuft, wird der aktuelle
 * GameState gestaffelt angefordert, bis ein frischer Broadcast eintrifft
 * (analog zur Wartelobby). Sind alle Versuche aufgebraucht ohne neuen
 * State, wird abgebrochen.
 *
 * Die Spielregel-Werte (Anzahl/Delay der Versuche) liegen in
 * [GameStateResyncLogic], damit sie unabhaengig von der Composable
 * testbar bleiben.
 */
@Composable
fun GameStateResyncEffect(
    connectionState: ConnectionState,
    session: GameSession
) {
    LaunchedEffect(connectionState) {
        if (connectionState != ConnectionState.Connected) return@LaunchedEffect
        val roomId = session.activeRoomId.value
        if (roomId.isBlank()) return@LaunchedEffect
        val before = session.gameState.value
        repeat(GameStateResyncLogic.RESYNC_REQUESTS) {
            session.endpoint.requestRoomState(roomId)
            delay(GameStateResyncLogic.RESYNC_DELAY_MS)
            if (session.gameState.value !== before) return@LaunchedEffect
        }
    }
}
