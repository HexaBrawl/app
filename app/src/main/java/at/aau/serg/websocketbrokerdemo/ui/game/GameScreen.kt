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
import at.aau.serg.websocketbrokerdemo.grid.HexGridLogic
import at.aau.serg.websocketbrokerdemo.grid.MapLayouts
import at.aau.serg.websocketbrokerdemo.network.GameSession
import at.aau.serg.websocketbrokerdemo.ui.game.bottomhud.BottomHud
import at.aau.serg.websocketbrokerdemo.ui.game.bottomhud.components.PlacementOverlay
import at.aau.serg.websocketbrokerdemo.ui.game.camera.CameraState
import at.aau.serg.websocketbrokerdemo.ui.game.tophud.TopHud
import at.aau.serg.websocketbrokerdemo.ui.mainmenu.GameMode
import androidx.compose.runtime.LaunchedEffect
import at.aau.serg.websocketbrokerdemo.ui.navigation.Screen

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

    // Wenn eine eigene Einheit selektiert ist, dunkle alle Felder ab,
    // die NICHT in Reichweite sind. Range = 2 nur wenn die Einheit auf
    // einem eigenen (Field.owner == player) Feld steht, sonst 1.
    val darkenedCells: Set<Pair<Int, Int>> = uiState.selected?.let { selected ->
        val reachable = GameScreenLogic.reachableCells(selected, fields, layout)
        if (reachable.isEmpty()) emptySet()
        else HexGridLogic.allCells(layout).toSet() -
            reachable -
            setOf(selected.x to selected.y)
    } ?: emptySet()

    LaunchedEffect(status) {
        if (status == GameStatus.FINISHED) {
            val winner = gameState?.winner
            val isWin = winner == localName
            val route = if (isWin) Screen.EndWin.route else Screen.EndLoss.route
            navController.navigate(route) {
                // Clear the backstack up to Home to avoid going back into the finished game
                popUpTo(Screen.Home.route) { inclusive = false }
            }
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
            darkenedCells = darkenedCells
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
    }
}
