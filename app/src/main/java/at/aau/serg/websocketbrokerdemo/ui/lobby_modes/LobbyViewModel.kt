package at.aau.serg.websocketbrokerdemo.ui.lobby_modes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.aau.serg.websocketbrokerdemo.network.GameSession
import at.aau.serg.websocketbrokerdemo.network.RoomApiClient
import at.aau.serg.websocketbrokerdemo.ui.mainmenu.GameMode
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel der Modus-Lobby.
 *
 * Haelt den UI-State (Dialog-Sichtbarkeit, Code-Eingabe, Error, Loading)
 * und delegiert die Entscheidungs-Logik an [LobbyRoomLogic].
 *
 * Die eigentliche Navigation wird ueber den [effects]-Flow an den
 * [LobbyScreen] signalisiert, damit das ViewModel frei von Compose-
 * Navigation-Abhaengigkeiten bleibt.
 */
class LobbyViewModel(
    private val apiClient: RoomApiClient = RoomApiClient(),
    private val session: GameSession? = null,
) : ViewModel() {

    private val _state = MutableStateFlow(LobbyState())

    /** Public State, vom Composable ueber collectAsStateWithLifecycle gelesen. */
    val state: StateFlow<LobbyState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<LobbyEffect>()
    /** Einmalige Effekte (Navigation, Fehler-Snackbars etc.) */
    val effects: SharedFlow<LobbyEffect> = _effects.asSharedFlow()

    // ---- Dialog-Lifecycle ----------------------------------------------

    /** Oeffnet den "Beitreten via Code"-Dialog mit leerem Eingabefeld. */
    fun openJoinDialog() {
        _state.value = LobbyState(showJoinDialog = true, code = "", error = null)
    }

    /** Schliesst den Dialog ohne weitere Aktion (Cancel). */
    fun closeJoinDialog() {
        _state.value = _state.value.copy(showJoinDialog = false, error = null)
    }

    // ---- Code-Eingabe --------------------------------------------------

    /**
     * Verarbeitet eine Code-Eingabe. Wendet [JoinByCodeLogic.normalize]
     * an, damit der State stets in einem gueltigen Format bleibt.
     */
    fun onCodeChange(rawInput: String) {
        _state.value = _state.value.copy(code = JoinByCodeLogic.normalize(rawInput), error = null)
    }

    // ---- Beitritts-Aktionen --------------------------------------------

    /** Erstellt einen privaten Raum fuer den gewaehlten Modus. */
    fun createPrivateGame(mode: GameMode) {
        if (_state.value.isLoading) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val room = apiClient.createRoom(mapUiToDataMode(mode))
            applyEffects(LobbyRoomLogic.effectsForCreateResult(room))
        }
    }

    /** Versucht via Code einem Raum beizutreten. */
    fun tryJoinByCode() {
        if (!LobbyRoomLogic.canAttemptJoin(_state.value)) return
        val code = _state.value.code

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val room = apiClient.findByCode(code)
            applyEffects(LobbyRoomLogic.effectsForJoinByCodeResult(room, code))
        }
    }

    // ---- Internes -------------------------------------------------------

    private suspend fun applyEffects(effects: List<LobbyEffect>) {
        _state.value = _state.value.copy(isLoading = false)
        effects.forEach { effect ->
            when (effect) {
                is LobbyEffect.SetRoomId -> session?.activeRoomId?.value = effect.roomId
                is LobbyEffect.CloseJoinDialog -> _state.value = _state.value.copy(showJoinDialog = false)
                is LobbyEffect.ShowError -> _state.value = _state.value.copy(error = effect.message)
                is LobbyEffect.NavigateToWaiting -> _effects.emit(effect)
            }
        }
    }

    private fun mapUiToDataMode(mode: GameMode): at.aau.serg.websocketbrokerdemo.data.serverside.GameMode =
        at.aau.serg.websocketbrokerdemo.data.serverside.GameMode.valueOf(mode.name)
}
