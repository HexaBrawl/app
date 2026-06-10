package at.aau.serg.websocketbrokerdemo.ui.lobby_modes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.aau.serg.websocketbrokerdemo.network.GameSession
import at.aau.serg.websocketbrokerdemo.network.RoomApiClient
import at.aau.serg.websocketbrokerdemo.ui.mainmenu.GameMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel der Modus-Lobby.
 *
 * Haelt den UI-State (Dialog-Sichtbarkeit, Code-Eingabe) und delegiert
 * die Entscheidungs-Logik an [LobbyRoomLogic].
 */
class LobbyViewModel(
    private val apiClient: RoomApiClient,
    private val session: GameSession,
) : ViewModel() {

    private val _state = MutableStateFlow(LobbyState())
    val state: StateFlow<LobbyState> = _state.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _lastError = MutableStateFlow<String?>(null)
    val lastError: StateFlow<String?> = _lastError.asStateFlow()

    // ---- Dialog-Lifecycle ----------------------------------------------

    /** Oeffnet den "Beitreten via Code"-Dialog mit leerem Eingabefeld. */
    fun openJoinDialog() {
        _state.value = _state.value.copy(showJoinDialog = true, code = "", error = null)
        _lastError.value = null
    }

    /** Schliesst den Dialog ohne weitere Aktion (Cancel). */
    fun closeJoinDialog() {
        _state.value = _state.value.copy(showJoinDialog = false, error = null)
        _lastError.value = null
    }

    // ---- Code-Eingabe --------------------------------------------------

    /**
     * Verarbeitet eine Code-Eingabe. Wendet [JoinByCodeLogic.normalize]
     * an, damit der State stets in einem gueltigen Format bleibt.
     */
    fun onCodeChange(rawInput: String) {
        _state.value = _state.value.copy(code = JoinByCodeLogic.normalize(rawInput), error = null)
        _lastError.value = null
    }

    // ---- Beitritts-Aktionen --------------------------------------------

    /** Erstellt einen privaten Raum fuer den gewaehlten Modus. */
    fun createRoom(mode: GameMode, onSuccess: () -> Unit) {
        if (_isLoading.value) return
        viewModelScope.launch {
            _isLoading.value = true
            _state.value = _state.value.copy(isLoading = true, error = null)
            _lastError.value = null
            try {
                val room = apiClient.createRoom(mapUiToDataMode(mode))
                apply(LobbyRoomLogic.effectsForCreateResult(room), onSuccess)
            } catch (e: Exception) {
                _isLoading.value = false
                _state.value = _state.value.copy(isLoading = false)
                _lastError.value = "Netzwerkfehler: ${e.message}"
            }
        }
    }

    /** Versucht via Code einem Raum beizutreten. */
    fun tryJoinByCodeAsync(onSuccess: () -> Unit) {
        if (!LobbyRoomLogic.canAttemptJoin(_state.value) || _isLoading.value) return
        val code = _state.value.code

        viewModelScope.launch {
            _isLoading.value = true
            _state.value = _state.value.copy(isLoading = true, error = null)
            _lastError.value = null
            try {
                val room = apiClient.findByCode(code)
                apply(LobbyRoomLogic.effectsForJoinByCodeResult(room, code), onSuccess)
            } catch (e: Exception) {
                _isLoading.value = false
                _state.value = _state.value.copy(isLoading = false)
                _lastError.value = "Netzwerkfehler: ${e.message}"
            }
        }
    }

    // ---- Internes -------------------------------------------------------

    /**
     * Reine Effect-Runner Funktion. Führt die fachlichen Entscheidungen aus
     * [LobbyRoomLogic] auf den State und die Session aus.
     */
    private fun apply(effects: List<LobbyEffect>, onSuccess: () -> Unit) {
        _isLoading.value = false
        _state.value = _state.value.copy(isLoading = false)
        effects.forEach { effect ->
            when (effect) {
                is LobbyEffect.SetRoomId -> {
                    session.activeRoomId.value = effect.roomId
                    // Neuer Raum: alle stale States aus vorherigen Spielen
                    // wegwerfen, damit die WaitingLobby nicht alte Spieler
                    // oder Fehler anzeigt.
                    session.gameState.value = null
                    session.lastError.value = null
                }
                is LobbyEffect.SetJoinCode -> session.activeJoinCode.value = effect.joinCode
                is LobbyEffect.CloseJoinDialog -> _state.value = _state.value.copy(showJoinDialog = false)
                is LobbyEffect.ShowError -> {
                    _lastError.value = effect.message
                    _state.value = _state.value.copy(error = effect.message)
                }
                is LobbyEffect.NavigateToWaiting -> onSuccess()
            }
        }
    }

    private fun mapUiToDataMode(mode: GameMode): at.aau.serg.websocketbrokerdemo.data.serverside.GameMode =
        at.aau.serg.websocketbrokerdemo.data.serverside.GameMode.valueOf(mode.name)
}
