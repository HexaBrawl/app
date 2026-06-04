package at.aau.serg.websocketbrokerdemo.ui.game

import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import at.aau.serg.websocketbrokerdemo.data.serverside.GameUnit
import at.aau.serg.websocketbrokerdemo.data.serverside.Move
import at.aau.serg.websocketbrokerdemo.network.GameSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel des GameScreens.
 *
 * Haelt die lokale Auswahl-Logik und reicht User-Eingaben (Taps) an die
 * GameSession durch. Die eigentliche Spielzustands-Verwaltung erfolgt
 * server-seitig -- das ViewModel beobachtet nur und sendet Moves.
 */
class GameViewModel(
    private val session: GameSession
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())

    /** Public State -- wird vom Composable ueber collectAsState gelesen. */
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    /**
     * Verarbeitet einen Tap auf eine Hex-Zelle.
     *
     * Delegiert die Entscheidung an [GameScreenLogic.decideTapAction]
     * und setzt entsprechend State / sendet einen Move.
     */
    fun onCellTapped(col: Int, row: Int, units: List<GameUnit>) {
        val localName = session.localPlayerName.value
        val currentSelected = _uiState.value.selected

        when (val action = GameScreenLogic.decideTapAction(
            col = col,
            row = row,
            units = units,
            localName = localName,
            currentlySelected = currentSelected
        )) {
            is GameScreenLogic.TapAction.Select -> {
                _uiState.value = _uiState.value.copy(selected = action.unit)
            }

            is GameScreenLogic.TapAction.ExecuteMove -> {
                sendMove(action.move)
                _uiState.value = _uiState.value.copy(selected = null)
            }

            GameScreenLogic.TapAction.Ignore -> Unit
        }
    }

    /**
     * Convenience-Helper fuer den Composable: wandelt eine Tap-Position
     * direkt in eine Zelle (oder null) um.
     */
    fun tapToCell(
        tapX: Float,
        tapY: Float,
        viewportSize: IntSize,
        pixelToCell: (Float, Float) -> Pair<Int, Int>?
    ): Pair<Int, Int>? =
        GameScreenLogic.tapToCell(tapX, tapY, viewportSize, pixelToCell)

    private fun sendMove(move: Move) {
        session.lastError.value = null
        session.endpoint.sendMove(session.activeRoomId.value, move)
    }
}
