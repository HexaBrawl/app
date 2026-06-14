package at.aau.serg.websocketbrokerdemo.ui.waiting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import at.aau.serg.websocketbrokerdemo.data.serverside.ErrorCode
import at.aau.serg.websocketbrokerdemo.data.serverside.GameStatus
import at.aau.serg.websocketbrokerdemo.data.serverside.PlayerColor
import at.aau.serg.websocketbrokerdemo.network.GameSession
import at.aau.serg.websocketbrokerdemo.ui.navigation.Screen
import kotlinx.coroutines.delay

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
    // Lokale Werte direkt aus dem observed State ableiten -- damit Compose
    // die Recomposition garantiert triggert, wenn der User Name, Farbe
    // oder Ready-Status aendert. Direkte Aufrufe von viewModel.localXyz
    // wurden zwar gelesen, aber nicht zuverlaessig fuer LaunchedEffect-Keys
    // erfasst.
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

    // Aktuellen Raum-Zustand aktiv anfordern.
    //
    // STOMP-Subscriptions liefern nur zukuenftige Broadcasts. Ein spaet
    // beitretender Spieler wuerde die bereits anwesenden Spieler und deren
    // belegte Farben sonst nie sehen -- er bliebe auf der Default-Farbe,
    // wuerde beim "Ready" mit COLOR_ALREADY_TAKEN abgelehnt und der Raum
    // erreicht nie die volle Spielerzahl (kein Auto-Start). Mit dem
    // angeforderten State greift der Auto-Reassign in
    // applyRemoteState, bevor der User "Ready" drueckt.
    //
    // Gestaffelte Wiederholung: der erste Request kann die Subscribe-
    // Registrierung am Broker knapp verpassen (Subscribe und Send laufen in
    // getrennten Coroutines). Wir fragen daher mehrfach, bis der erste
    // Broadcast eintrifft -- danach stoppt die Schleife. Die Requests sind
    // idempotent (der Server rebroadcastet nur).
    LaunchedEffect(session.activeRoomId.value) {
        val roomId = session.activeRoomId.value
        if (roomId.isBlank()) return@LaunchedEffect
        repeat(MAX_ROOM_STATE_REQUESTS) {
            if (session.gameState.value != null) return@LaunchedEffect
            session.endpoint.requestRoomState(roomId)
            delay(ROOM_STATE_REQUEST_DELAY_MS)
        }
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
            session.sessionRepository.playerName = localName
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

        // Lokalen Spieler im Server-State erkennen -> Name + Farbe locken.
        // Wir verlassen uns nicht auf den joinGame-Send, sondern auf den
        // tatsaechlichen Broadcast: wenn der Server den Spieler ablehnt
        // (z.B. COLOR_ALREADY_TAKEN), taucht er nicht in der Liste auf
        // und der Lock bleibt offen, sodass der User die Farbe wechseln
        // kann.
        if (state.players.any { it.name == localName }) {
            viewModel.markJoinedServer()
        }

        // Komplette Player-Objekte (mit Server-Farbe) weitergeben, damit
        // der Color-Picker im Wartelobby-UI konsistent zur Realitaet ist.
        val remotePlayers = state.players.filter { it.name != localName }

        viewModel.applyRemoteState(remotePlayers)
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

    // Server-Fehler beobachten und sinnvoll behandeln.
    //
    // Speziell COLOR_ALREADY_TAKEN: der lokale Spieler hat eine Farbe
    // gewaehlt, die schon belegt war. Wir setzen den ready-Status zurueck
    // (damit der User die Farbe wieder wechseln kann) und zeigen eine
    // Snackbar mit einem klaren Hinweis.
    //
    // Andere Fehler werden 1:1 als Snackbar gezeigt; die App bleibt
    // ansonsten im aktuellen State.
    val lastError by session.lastError
    LaunchedEffect(lastError) {
        val error = lastError ?: return@LaunchedEffect
        when (error.errorCode) {
            ErrorCode.COLOR_ALREADY_TAKEN -> {
                viewModel.clearLocalReady()
                viewModel.showError(error.message)
            }
            else -> {
                viewModel.showError(error.message)
            }
        }
        // Error in der Session wieder loeschen, sonst triggert derselbe
        // Effect bei jeder Recomposition erneut.
        session.lastError.value = null
    }
}

/**
 * Maximale Anzahl gestaffelter /init-Anfragen beim Betreten der Wartelobby,
 * bis der erste GameState-Broadcast eingetroffen ist.
 */
private const val MAX_ROOM_STATE_REQUESTS = 5

/** Abstand zwischen zwei /init-Anfragen in Millisekunden. */
private const val ROOM_STATE_REQUEST_DELAY_MS = 400L
