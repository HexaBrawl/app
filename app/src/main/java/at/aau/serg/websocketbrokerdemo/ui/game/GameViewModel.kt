package at.aau.serg.websocketbrokerdemo.ui.game

import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import at.aau.serg.websocketbrokerdemo.audio.MusicManager
import at.aau.serg.websocketbrokerdemo.data.serverside.GameUnit
import at.aau.serg.websocketbrokerdemo.data.serverside.Move
import at.aau.serg.websocketbrokerdemo.data.serverside.UnitType
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
     * Aktiviert den Platzierungs-Modus fuer eine gekaufte Einheit. Eine
     * eventuelle Selection wird verworfen, weil beide State-Felder sich
     * gegenseitig ausschliessen.
     */
    fun startPlacement(type: UnitType) {
        _uiState.value = GameUiState(selected = null, placementMode = type)
    }

    /** Bricht den Platzierungs-Modus ohne Kauf ab. */
    fun cancelPlacement() {
        _uiState.value = _uiState.value.copy(placementMode = null)
    }

    /**
     * Verarbeitet einen Tap auf eine Hex-Zelle.
     *
     * Delegiert die Entscheidung an [GameScreenLogic.decideTapAction]
     * und setzt entsprechend State / sendet einen Move.
     */
    fun onCellTapped(col: Int, row: Int, units: List<GameUnit>) {
        val localName = session.localPlayerName.value
        val state = _uiState.value

        when (val action = GameScreenLogic.decideTapAction(
            col = col,
            row = row,
            units = units,
            localName = localName,
            currentlySelected = state.selected,
            placementMode = state.placementMode
        )) {
            is GameScreenLogic.TapAction.Select -> {
                _uiState.value = state.copy(selected = action.unit)
            }

            is GameScreenLogic.TapAction.ExecuteMove -> {
                if (GameScreenLogic.isAttackMove(action.move, units, localName)) {
                    MusicManager.playSwordBlock()
                }
                sendMove(action.move)
                _uiState.value = state.copy(selected = null)
            }

            is GameScreenLogic.TapAction.PlaceUnit -> {
                val name = localName ?: return
                session.endpoint.buyUnit(
                    roomId = session.activeRoomId.value,
                    playerName = name,
                    type = action.type,
                    x = action.x,
                    y = action.y
                )
                _uiState.value = state.copy(placementMode = null)
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
