package at.aau.serg.websocketbrokerdemo.ui.lobby_modes

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel der Modus-Lobby.
 *
 * Haelt den UI-State (Dialog-Sichtbarkeit, Code-Eingabe) und stellt
 * Handler fuer die drei Lobby-Aktionen bereit. Die eigentliche
 * Navigation laeuft nicht durch das ViewModel -- die Aufrufer-Seite
 * (LobbyScreen) navigiert mit dem NavController, sobald das ViewModel
 * signalisiert "joinen ok". So bleibt das ViewModel frei von Compose-
 * Navigation-Abhaengigkeiten und ist sauber unit-testbar.
 *
 * Da der Dialog-State (Code-Eingabe) im ViewModel lebt statt im
 * Composable, wird er beim Wiederoeffnen des Dialogs aktiv geleert --
 * siehe [openJoinDialog] und [closeJoinDialog].
 */
class LobbyViewModel : ViewModel() {

    private val _state = MutableStateFlow(LobbyState())

    /** Public State, vom Composable ueber collectAsStateWithLifecycle gelesen. */
    val state: StateFlow<LobbyState> = _state.asStateFlow()

    // ---- Dialog-Lifecycle ----------------------------------------------

    /** Oeffnet den "Beitreten via Code"-Dialog mit leerem Eingabefeld. */
    fun openJoinDialog() {
        _state.value = LobbyState(showJoinDialog = true, code = "")
    }

    /** Schliesst den Dialog ohne weitere Aktion (Cancel). */
    fun closeJoinDialog() {
        _state.value = _state.value.copy(showJoinDialog = false)
    }

    // ---- Code-Eingabe --------------------------------------------------

    /**
     * Verarbeitet eine Code-Eingabe. Wendet [JoinByCodeLogic.normalize]
     * an, damit der State stets in einem gueltigen Format bleibt.
     */
    fun onCodeChange(rawInput: String) {
        _state.value = _state.value.copy(code = JoinByCodeLogic.normalize(rawInput))
    }

    // ---- Beitritts-Aktionen --------------------------------------------

    /**
     * Versucht beizutreten. Liefert true, wenn der Code gueltig ist und
     * der Aufrufer (Screen) navigieren darf. Im Erfolgsfall wird der
     * Dialog automatisch geschlossen.
     */
    fun tryJoinByCode(): Boolean {
        if (!_state.value.canJoin) return false
        _state.value = _state.value.copy(showJoinDialog = false)
        return true
    }
}
