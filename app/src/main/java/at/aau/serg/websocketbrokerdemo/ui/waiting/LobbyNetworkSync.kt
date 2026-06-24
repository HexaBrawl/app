package at.aau.serg.websocketbrokerdemo.ui.waiting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import at.aau.serg.websocketbrokerdemo.data.serverside.PlayerColor
import at.aau.serg.websocketbrokerdemo.network.GameSession
import at.aau.serg.websocketbrokerdemo.ui.navigation.Screen
import kotlinx.coroutines.delay

/**
 * Verbindet das Wartelobby-ViewModel mit dem GameSession-Netzwerk.
 *
 * Reine Side-Effect-/Wiring-Schicht: Subscription verwalten, Server-Calls
 * absetzen und Navigation ausloesen. Saemtliche Entscheidungen (wann anmelden,
 * wer ist remote, wann navigieren, welcher Fehler braucht Reselection) liegen
 * in [LobbyNetworkLogic] -- pure und getestet.
 *
 * Verantwortungen:
 *  - GameState-Subscription verwalten (DisposableEffect)
 *  - Aktuellen Raum-Zustand beim Eintritt anfordern (Resync)
 *  - Lokalen Spieler beim Server anmelden, sobald "Ready" geklickt ist
 *  - Server-Updates an [WaitingLobbyViewModel.applyRemoteState] weiterreichen
 *  - Zum GameScreen navigieren, sobald Countdown durch + Spiel IN_PROGRESS
 *  - Server-Fehler behandeln (Snackbar; bei Farb-/Namens-Konflikt Ready-Reset)
 */
@Composable
fun LobbyNetworkSync(
    session: GameSession,
    viewModel: WaitingLobbyViewModel,
    navController: NavController
) {
    val viewModelState by viewModel.state.collectAsStateWithLifecycle()
    // Lokale Werte aus dem observed State ableiten, damit Compose die
    // Recomposition (und damit die LaunchedEffect-Keys) zuverlaessig triggert.
    val localSlot = viewModelState.slots.firstOrNull { it.isLocal }
    val localName = localSlot?.name.orEmpty()
    val localColor = localSlot?.color ?: PlayerColor.RED
    val localReady = localSlot?.ready ?: false

    DisposableEffect(session.activeRoomId.value) {
        val job = session.endpoint.subscribeToGameState(session.activeRoomId.value) { state ->
            session.gameState.value = state
        }
        onDispose { job.cancel() }
    }

    // Aktuellen Raum-Zustand aktiv anfordern (STOMP liefert nur zukuenftige
    // Broadcasts). Gestaffelt, bis der erste Broadcast eintrifft -- danach
    // stoppt die Schleife. Idempotent (Server rebroadcastet nur).
    LaunchedEffect(session.activeRoomId.value) {
        val roomId = session.activeRoomId.value
        if (roomId.isBlank()) return@LaunchedEffect
        repeat(LobbyNetworkLogic.RESYNC_REQUESTS) {
            if (session.gameState.value != null) return@LaunchedEffect
            session.endpoint.requestRoomState(roomId)
            delay(LobbyNetworkLogic.RESYNC_DELAY_MS)
        }
    }

    // Beim Server anmelden, sobald der User "Ready" geklickt hat (Name/Farbe
    // sind dann gewaehlt).
    LaunchedEffect(localReady, localName, localColor) {
        if (LobbyNetworkLogic.shouldJoin(localReady, localName, session.activeRoomId.value)) {
            session.localPlayerName.value = localName
            session.sessionRepository.playerName = localName
            session.endpoint.joinGame(
                roomId = session.activeRoomId.value,
                playerName = localName,
                color = localColor
            )
        }
    }

    val gameState by session.gameState

    // Server-Updates ans ViewModel weiterreichen. Navigation passiert bewusst
    // erst nach dem Countdown (siehe LaunchedEffect unten).
    LaunchedEffect(gameState) {
        val state = gameState ?: return@LaunchedEffect
        if (LobbyNetworkLogic.isLocalPlayerPresent(state.players, localName)) {
            viewModel.markJoinedServer()
        }
        viewModel.applyRemoteState(LobbyNetworkLogic.remotePlayers(state.players, localName))
    }

    // Zum GameScreen navigieren -- erst NACHDEM der Countdown durchgelaufen ist
    // UND der Server IN_PROGRESS meldet.
    LaunchedEffect(viewModelState.countdownComplete, gameState?.status) {
        if (LobbyNetworkLogic.shouldNavigateToGame(viewModelState.countdownComplete, gameState?.status)) {
            navController.navigate(Screen.Game.route) {
                popUpTo(Screen.Home.route) { inclusive = false }
            }
        }
    }

    // Server-Fehler behandeln: Snackbar; bei Farb-/Namens-Konflikt zusaetzlich
    // Ready zuruecksetzen, damit Name/Farbe wieder editierbar sind.
    val lastError by session.lastError
    LaunchedEffect(lastError) {
        val error = lastError ?: return@LaunchedEffect
        if (LobbyNetworkLogic.requiresReselection(error.errorCode)) {
            viewModel.clearLocalReady()
        }
        viewModel.showError(error.message)
        // Error loeschen, sonst triggert derselbe Effect bei jeder Recomposition.
        session.lastError.value = null
    }
}
