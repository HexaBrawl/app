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
 *  - Beim Aufruf: lokalen Spieler beim Server anmelden
 *  - GameState-Subscription verwalten (DisposableEffect)
 *  - Server-Updates an [WaitingLobbyViewModel.applyRemoteState] weiterreichen
 *  - Bei status = IN_PROGRESS zum GameScreen navigieren
 *
 * Eigene Composable-Funktion damit die Side-Effects nicht direkt im
 * Screen-Composable haengen -- das haelt den Screen logikfrei.
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

    LaunchedEffect(localName) {
        if (localName.isNotBlank()) {
            session.localPlayerName.value = localName
            session.endpoint.joinGame(session.activeRoomId.value, localName)
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
