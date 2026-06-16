package at.aau.serg.websocketbrokerdemo.ui.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import at.aau.serg.websocketbrokerdemo.data.serverside.GameStatus
import at.aau.serg.websocketbrokerdemo.grid.MapLayouts
import at.aau.serg.websocketbrokerdemo.network.ConnectionState
import at.aau.serg.websocketbrokerdemo.network.GameSession
import at.aau.serg.websocketbrokerdemo.ui.game.bottomhud.BottomHud
import at.aau.serg.websocketbrokerdemo.ui.game.bottomhud.components.PlacementOverlay
import at.aau.serg.websocketbrokerdemo.ui.game.camera.CameraState
import at.aau.serg.websocketbrokerdemo.ui.game.tophud.TopHud
import at.aau.serg.websocketbrokerdemo.ui.game.tophud.components.DisconnectedPlayerOverlay
import at.aau.serg.websocketbrokerdemo.ui.game.tophud.components.ReconnectingOverlay
import at.aau.serg.websocketbrokerdemo.ui.mainmenu.GameMode
import androidx.compose.runtime.LaunchedEffect
import at.aau.serg.websocketbrokerdemo.ui.navigation.Screen
import kotlinx.coroutines.delay

/**
 * GameScreen -- der eigentliche Spielbildschirm.
 *
 * Verbindet Spielkarte und Top-HUD. Das pendingGift aus dem GameState
 * wird ans HUD weitergereicht, damit Steal-Popup und Waiting-Overlay
 * korrekt erscheinen koennen.
 * Verbindet Spielkarte und Top-HUD. Da die Einstellungen jetzt in einem
 * In-Game-Popup landen (statt zum SettingsScreen zu navigieren),
 * braucht der GameScreen keinen NavController mehr.
 *
 * Reine UI-Schicht. Reicht den serverseitigen GameState an die
 * Sub-Composables weiter und delegiert Taps an das ViewModel.
 * Die Hex-Felder (fields) gehen mit an die GameMap, damit eroberte
 * Zellen in der Spielerfarbe markiert werden (subssue #123).
 *
 * Erhaelt den Spielmodus per Parameter, weil der Server bisher keine
 * Map-Konfiguration zurueck schickt -- das Frontend leitet das Brett
 * aus dem in der Wartelobby gewaehlten Modus ab.
 */
@Composable
fun GameScreen(
    session: GameSession,
    mode: GameMode = GameMode.DUAL_VALLEY,
    navController: NavController
) {
    val viewModel: GameViewModel = viewModel(
        factory = viewModelFactory {
            initializer { GameViewModel(session) }
        }
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val gameState by session.gameState

    val layout = remember(mode) { MapLayouts.forMode(mode) }
    val camera = remember { CameraState(mapSizeFactor = 1.0f) }

    DisposableEffect(session.activeRoomId.value) {
        val job = session.endpoint.subscribeToGameState(session.activeRoomId.value) { state ->
            session.gameState.value = state
        }
        onDispose { job.cancel() }
    }

    val units = gameState?.units.orEmpty()
    val fields = gameState?.fields.orEmpty()
    val players = gameState?.players.orEmpty()
    val localName = session.localPlayerName.value
    val pendingGift = gameState?.pendingGift
    val currentTurn = gameState?.currentTurn
    val status = gameState?.status ?: GameStatus.WAITING_FOR_PLAYERS
    val connectionState by session.connectionState.collectAsStateWithLifecycle()
    val disconnectedNames = GameScreenLogic.disconnectedOtherPlayerNames(players, localName)

    // Feld-Hervorhebung (gueltige Felder leuchten auf, der Rest wird
    // abgedunkelt) wird vollstaendig in der reinen Logik berechnet -- der
    // Composable reicht nur den State rein und das Ergebnis ans Rendering.
    val highlighting = GameScreenLogic.cellHighlighting(
        placementMode = uiState.placementMode,
        selected = uiState.selected,
        fields = fields,
        units = units,
        localName = localName,
        layout = layout
    )

    LaunchedEffect(status, gameState?.players) {
        val state = gameState ?: return@LaunchedEffect
        val name = localName ?: return@LaunchedEffect

        // Im 3-/4-Spieler-Modus bleibt der GameStatus nach dem Ausscheiden
        // eines Spielers auf IN_PROGRESS — der Verlierer wird vom Server
        // einfach aus state.players entfernt (siehe GameService.eliminatePlayer).
        // Daher zusaetzlich pruefen, ob der lokale Spieler noch in der Liste
        // steht; falls nicht, ist er raus und gehoert auf den LossScreen.
        val iAmEliminated = state.players.none { it.name == name }
        val gameOver = status == GameStatus.FINISHED

        if (iAmEliminated || gameOver) {
            // Spiel ist vorbei -- die Reconnect-Identitaet wird nicht
            // mehr gebraucht. Wir loeschen sie hier (statt im EndScreen),
            // damit ein direktes "App wegswipen" auf dem EndScreen kein
            // /leave fuer einen toten Raum mehr ausloest.
            session.sessionRepository.clear()
            val isWin = state.winner == name
            val route = if (isWin) Screen.EndWin.route else Screen.EndLoss.route
            navController.navigate(route) {
                // Clear the backstack up to Home to avoid going back into the finished game
                popUpTo(Screen.Home.route) { inclusive = false }
            }
        }
    }

    // Resync nach (Re-)Connect: STOMP-Subscriptions liefern nur ZUKUENFTIGE
    // Broadcasts -- waehrend einer Trennung verpasste Aenderungen (Zugwechsel,
    // pendingGift, ...) fehlen sonst, der Client haengt oder zeigt kein Popup.
    // Sobald die Verbindung steht und ein Match laeuft, fordern wir den
    // aktuellen GameState gestaffelt an, bis ein frischer Broadcast eintrifft
    // (analog zur Wartelobby).
    LaunchedEffect(connectionState) {
        if (connectionState != ConnectionState.Connected) return@LaunchedEffect
        val roomId = session.activeRoomId.value
        if (roomId.isBlank()) return@LaunchedEffect
        val before = session.gameState.value
        repeat(GAME_RESYNC_REQUESTS) {
            session.endpoint.requestRoomState(roomId)
            delay(GAME_RESYNC_DELAY_MS)
            if (session.gameState.value !== before) return@LaunchedEffect
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GameMap(
            layout = layout,
            units = units,
            buildings = gameState?.buildings.orEmpty(),
            fields = fields,
            players = players,
            camera = camera,
            onCellTapped = { tapX, tapY, pixelToCell ->
                val cell = viewModel.tapToCell(tapX, tapY, camera.viewportSize.value, pixelToCell)
                if (cell != null) {
                    viewModel.onCellTapped(cell.first, cell.second, units)
                }
            },
            darkenedCells = highlighting.darkened,
            highlightedCells = highlighting.highlighted
        )

        TopHud(
            players = players,
            units = units,
            fields = fields,
            localName = localName,
            pendingGift = pendingGift,
            session = session,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        BottomHud(
            players = players,
            localName = localName,
            currentTurn = currentTurn,
            status = status,
            placementMode = uiState.placementMode,
            onBuyFarm = {
                localName?.let { name ->
                    session.endpoint.buyFarm(session.activeRoomId.value, name)
                }
            },
            onSelectUnit = { type ->
                viewModel.startPlacement(type)
            },
            onEndTurn = {
                localName?.let { name ->
                    session.endpoint.endTurn(session.activeRoomId.value, name)
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        uiState.placementMode?.let { type ->
            PlacementOverlay(
                type = type,
                onCancel = viewModel::cancelPlacement,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }

        // Eigener Reconnect-Overlay liegt OBEN auf allem (auch ueber den
        // DisconnectedPlayerOverlay), damit der lokale Spieler im Worst
        // Case zuerst den eigenen Status sieht.
        if (connectionState != ConnectionState.Connected) {
            ReconnectingOverlay(
                state = connectionState,
                onRetry = { session.onRetryConnect() },
                onBackToMenu = {
                    session.sessionRepository.clear()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                }
            )
        } else if (disconnectedNames.isNotEmpty()) {
            DisconnectedPlayerOverlay(disconnectedNames = disconnectedNames)
        }
    }
}

/** Anzahl gestaffelter /init-Anfragen fuer den Resync nach (Re-)Connect. */
private const val GAME_RESYNC_REQUESTS = 5

/** Abstand zwischen zwei Resync-Anfragen in Millisekunden. */
private const val GAME_RESYNC_DELAY_MS = 400L
