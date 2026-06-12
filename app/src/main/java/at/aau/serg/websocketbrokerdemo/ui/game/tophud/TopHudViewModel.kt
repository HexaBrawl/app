package at.aau.serg.websocketbrokerdemo.ui.game.tophud

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel des Top-HUDs.
 *
 * Verwaltet ausschliesslich die Popup-Sichtbarkeit. Gold- und Income-
 * Werte kommen direkt vom GameState, nicht aus diesem ViewModel.
 */
class TopHudViewModel : ViewModel() {

    private val _state = MutableStateFlow(TopHudState())

    val state: StateFlow<TopHudState> = _state.asStateFlow()

    /** Oeffnet das Menue-Popup. */
    fun openMenu() {
        _state.value = TopHudState(popup = HudPopup.Menu)
    }

    /** Wechselt vom Menue zum Info-Popup. */
    fun showInfo() {
        _state.value = TopHudState(popup = HudPopup.Info)
    }

    /** Wechselt vom Menue zum In-Game-Settings-Popup. */
    fun showSettings() {
        _state.value = TopHudState(popup = HudPopup.Settings)
    }

    /**
     * Oeffnet das Einkommen-Detail-Popup. Wird durch Klick auf die
     * Einkommen-Box im Top-HUD ausgeloest.
     */
    fun showIncome() {
        _state.value = TopHudState(popup = HudPopup.Income)
    }

    /** Schliesst alle Popups. */
    fun closePopup() {
        _state.value = TopHudState(popup = HudPopup.None)
    }
}
