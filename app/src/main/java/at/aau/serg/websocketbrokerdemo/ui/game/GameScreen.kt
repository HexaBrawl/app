package at.aau.serg.websocketbrokerdemo.ui.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import at.aau.serg.websocketbrokerdemo.grid.MapLayouts
import at.aau.serg.websocketbrokerdemo.network.GameSession
import at.aau.serg.websocketbrokerdemo.ui.game.camera.CameraState
import at.aau.serg.websocketbrokerdemo.ui.game.components.DebugStatusPanel
import at.aau.serg.websocketbrokerdemo.ui.game.components.GameMap
import at.aau.serg.websocketbrokerdemo.ui.mainmenu.GameMode

/**
 * GameScreen -- der eigentliche Spielbildschirm.
 *
 * Reine UI-Schicht. Verantwortlich nur fuer:
 *  - Spielkarte mit Hex-Grid und Kamera rendern (siehe [GameMap])
 *  - Taps an [GameViewModel] weiterleiten
 *  - Optional Debug-Statusleiste einblenden (siehe [DebugStatusPanel],
 *    nur wenn [DEBUG_HUD] aktiv ist)
 *
 * Erhaelt den Spielmodus per Parameter, weil der Server bisher keine
 * Map-Konfiguration zurueck schickt -- das Frontend leitet das Brett
 * aus dem in der Wartelobby gewaehlten Modus ab.
 */
@Composable
fun GameScreen(
    session: GameSession,
    mode: GameMode = GameMode.DUAL_VALLEY
) {
    val viewModel: GameViewModel = viewModel(
        factory = viewModelFactory {
            initializer { GameViewModel(session) }
        }
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val gameState by session.gameState
    val lastError by session.lastError
    val localName = session.localPlayerName.value

    val layout = remember(mode) { MapLayouts.forMode(mode) }
    val camera = remember { CameraState(mapSizeFactor = 1.0f) }

    DisposableEffect(session.activeRoomId.value) {
        val job = session.endpoint.subscribeToGameState(session.activeRoomId.value) { state ->
            session.gameState.value = state
        }
        onDispose { job.cancel() }
    }

    val units = gameState?.units.orEmpty()

    Box(modifier = Modifier.fillMaxSize()) {
        GameMap(
            layout = layout,
            units = units,
            camera = camera,
            onCellTapped = { tapX, tapY, pixelToCell ->
                val cell = viewModel.tapToCell(tapX, tapY, camera.viewportSize.value, pixelToCell)
                if (cell != null) {
                    viewModel.onCellTapped(cell.first, cell.second, units)
                }
            }
        )
    }

    if (DEBUG_HUD) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.weight(1f))
            DebugStatusPanel(
                localName = localName,
                gameState = gameState,
                units = units,
                lastTap = uiState.lastTap,
                lastMove = uiState.lastMove,
                lastError = lastError,
                selected = uiState.selected,
                onClearError = { viewModel.clearError() },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
