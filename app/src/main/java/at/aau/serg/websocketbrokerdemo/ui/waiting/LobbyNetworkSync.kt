package at.aau.serg.websocketbrokerdemo.ui.waiting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import at.aau.serg.websocketbrokerdemo.data.serverside.GameStatus
import at.aau.serg.websocketbrokerdemo.network.GameSession
import at.aau.serg.websocketbrokerdemo.ui.navigation.Screen

/**
 * Verbindet das Wartelobby-ViewModel mit dem GameSession-Netzwerk.
 *
 * Verantwortungen:
 *  - GameState-Subscription verwalten (DisposableEffect)
 *  - Server-Updates an [WaitingLobbyViewModel.applyRemoteState]
 *    weiterreichen
 *  - Lokalen Spieler erst dann beim Server anmelden, wenn der User
 *    "Ready" geklickt hat -- vorher hat er Zeit, Name und Farbe zu
 *    waehlen
 *  - Zum GameScreen navigieren, sobald der Countdown durchgelaufen ist
 *    UND der Server den Spielstatus auf IN_PROGRESS gesetzt hat
 */
@Composable
fun LobbyNetworkSync(
    session: GameSession,
    viewModel: WaitingLobbyViewModel,
    navController: NavController
) {
    val viewModelState by viewModel.state.collectAsStateWithLifecycle()
    val localName = viewModel.localName
    val localColor = viewModel.localColor
    val localReady = viewModel.localReady

    DisposableEffect(session.activeRoomId.value) {
        val job = session.endpoint.subscribeToGameState(session.activeRoomId.value) { state ->
            session.gameState.value = state
        }
        onDispose { job.cancel() }
    }

    // Anmelden erst wenn der User "Ready" geklickt hat.
    //
    // Der User hat in der WaitingLobby zuvor seinen Namen und seine Farbe
    // gewaehlt. Der Klick auf "Ready" setzt im lokalen Slot ready=true
    // und triggert diesen Effect. Die gewaehlte Farbe wird mitgegeben --
    // bei Konflikt (zwei Spieler waehlen die gleiche Farbe) lehnt der
    // Server mit COLOR_ALREADY_TAKEN ab; das Error-Handling dafuer kommt
    // als Follow-up.
    LaunchedEffect(localReady, localName, localColor) {
        if (localReady && localName.isNotBlank() && session.activeRoomId.value.isNotBlank()) {
            session.localPlayerName.value = localName
            session.endpoint.joinGame(
                roomId = session.activeRoomId.value,
                playerName = localName,
                color = localColor
            )
        }
    }

    val gameState by session.gameState

    // Server-Updates an das ViewModel weiterreichen (Slots, Spielerliste).
    // Navigation passiert bewusst NICHT hier, sondern erst nachdem der
    // Countdown im ViewModel abgelaufen ist (siehe LaunchedEffect unten).
    LaunchedEffect(gameState) {
        val state = gameState ?: return@LaunchedEffect

        val remotePlayerNames = state.players
            .filter { it.name != localName }
            .map { it.name }

        viewModel.applyRemoteState(remotePlayerNames)
    }

    // Zum GameScreen navigieren -- aber erst NACHDEM der Countdown
    // wirklich durchgelaufen ist. So sieht der User die 3-2-1-Anzeige,
    // auch wenn der Server (Auto-Start sobald maxPlayers erreicht) den
    // Status schon vorher auf IN_PROGRESS gesetzt hat.
    LaunchedEffect(viewModelState.countdownComplete, gameState?.status) {
        val status = gameState?.status ?: return@LaunchedEffect
        if (viewModelState.countdownComplete && status == GameStatus.IN_PROGRESS) {
            navController.navigate(Screen.Game.route) {
                popUpTo(Screen.Home.route) { inclusive = false }
            }
        }
    }
}
