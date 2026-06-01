package at.aau.serg.websocketbrokerdemo.ui.mainmenu

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel für den MainMenuScreen.
 *
 * Verwaltet den UI-State (welches Popup ist offen, welcher Modus wurde
 * angetippt) und überlebt damit Configuration Changes (z. B. Rotation).
 *
 * Hat KEINE Coroutinen/Repository-Abhängigkeiten -- daher ein einfacher
 * ViewModel (nicht AndroidViewModel) und kein viewModelScope nötig.
 */
class MainMenuViewModel : ViewModel() {

    private val _state = MutableStateFlow(MainMenuState())
    val state: StateFlow<MainMenuState> = _state.asStateFlow()

    /** User hat einen X-Hotspot angetippt -> Bestätigungs-Popup anzeigen. */
    fun onHotspotTapped(mode: GameMode) {
        _state.update { it.copy(pendingMode = mode) }
    }

    /** User hat im Bestätigungs-Popup abgebrochen. */
    fun onDismissPendingMode() {
        _state.update { it.copy(pendingMode = null) }
    }

    /**
     * User hat das Bestätigungs-Popup bestätigt -- Popup schließen.
     * Die eigentliche Navigation übernimmt der Composable über
     * [MainMenuLogic.navigateToLobby], weil NavController eine UI-
     * Abhängigkeit ist und nicht ins ViewModel gehört.
     */
    fun onConfirmPendingMode() {
        _state.update { it.copy(pendingMode = null) }
    }

    /** User hat den Info-Button angetippt -> Info-Dialog anzeigen. */
    fun onInfoClicked() {
        _state.update { it.copy(showInfo = true) }
    }

    /** User hat den Info-Dialog geschlossen. */
    fun onDismissInfo() {
        _state.update { it.copy(showInfo = false) }
    }
}
