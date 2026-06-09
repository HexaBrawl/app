package at.aau.serg.websocketbrokerdemo.ui.waiting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import at.aau.serg.websocketbrokerdemo.data.serverside.GameStatus
import at.aau.serg.websocketbrokerdemo.network.GameSession
import at.aau.serg.websocketbrokerdemo.ui.navigation.Screen

/**
 * Verbindet das Wartelobby-ViewModel mit dem GameSession-Netzwerk.
 *
 * Verantwortungen:
 *  - Beim Aufruf: lokalen Spieler + gewaehlte Farbe beim Server anmelden
 *  - GameState-Subscription verwalten (DisposableEffect)
 *  - Server-Updates an [WaitingLobbyViewModel.applyRemoteState]
 *    weiterreichen
 *  - Bei status = IN_PROGRESS zum GameScreen navigieren
 */
@Composable
fun LobbyNetworkSync(
    session: GameSession,
    viewModel: WaitingLobbyViewModel,
    navController: NavController
) {
    val localName = viewModel.localName

    DisposableEffect(session.activeRoomId.value) {
        val job = session.endpoint.subscribeToGameState(session.activeRoomId.value) { state ->
            session.gameState.value = state
        }
        onDispose { job.cancel() }
    }

    // Anmelden sobald der lokale Name feststeht.
    //
    // Die Farbe wird BEWUSST nicht mitgeschickt (color = null). Der Server
    // vergibt selbst eine freie Farbe und broadcasted sie ueber den
    // GameState an alle Spieler im Raum. Damit gibt es keinen Konflikt
    // mehr, wenn beide Clients lokal mit dem RED-Default ihres Slots
    // initialisiert werden -- der zweite Spieler wuerde sonst vom Server
    // mit COLOR_ALREADY_TAKEN abgelehnt und taucht in keinem Raum auf.
    LaunchedEffect(localName) {
        if (localName.isNotBlank()) {
            session.localPlayerName.value = localName
            session.endpoint.joinGame(
                roomId = session.activeRoomId.value,
                playerName = localName,
                color = null
            )
        }
    }

    val gameState by session.gameState
    LaunchedEffect(gameState) {
        val state = gameState ?: return@LaunchedEffect

        val remotePlayerNames = state.players
            .filter { it.name != localName }
            .map { it.name }

        viewModel.applyRemoteState(remotePlayerNames)

        if (state.status == GameStatus.IN_PROGRESS) {
            navController.navigate(Screen.Game.route) {
                popUpTo(Screen.Home.route) { inclusive = false }
            }
        }
    }
}
