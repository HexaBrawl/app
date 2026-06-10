package at.aau.serg.websocketbrokerdemo.ui.waiting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.aau.serg.websocketbrokerdemo.data.serverside.Player
import at.aau.serg.websocketbrokerdemo.data.serverside.PlayerColor
import at.aau.serg.websocketbrokerdemo.ui.mainmenu.GameMode
import at.aau.serg.websocketbrokerdemo.ui.waiting.model.PlayerSlot
import at.aau.serg.websocketbrokerdemo.ui.waiting.model.SlotStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel der Wartelobby.
 *
 * Haelt die Slot-Liste, reagiert auf Eingaben des lokalen Spielers
 * (Name aendern, Farbe waehlen, ready toggeln) und startet einen
 * 3-Sekunden-Countdown sobald alle Slots besetzt und bereit sind.
 *
 * Server-Synchronisation laeuft separat ueber LobbyNetworkSync --
 * das ViewModel selbst kennt das Netzwerk nicht. Stattdessen ruft der
 * Screen [applyRemoteState] auf, wenn vom Server neue Daten kommen.
 */
class WaitingLobbyViewModel(
    private val mode: GameMode,
    private val initialRoomId: String = "",
    private val initialJoinCode: String = ""
) : ViewModel() {

    private val _state = MutableStateFlow(
        WaitingLobbyState(
            roomId = initialRoomId,
            joinCode = initialJoinCode,
            slots = WaitingLobbyLogic.createInitialSlots(mode)
        )
    )

    val state: StateFlow<WaitingLobbyState> = _state.asStateFlow()

    private var countdownJob: Job? = null

    /** Der lokale Spieler-Name (fuer NetworkSync zum Anmelden beim Server). */
    val localName: String
        get() = _state.value.slots.firstOrNull { it.isLocal }?.name.orEmpty()

    /** Die vom lokalen Spieler gewaehlte Farbe (fuer NetworkSync). */
    val localColor: PlayerColor
        get() = _state.value.slots.firstOrNull { it.isLocal }?.color ?: PlayerColor.RED

    /** Ob der lokale Spieler bereits "Ready" geklickt hat (fuer NetworkSync). */
    val localReady: Boolean
        get() = _state.value.slots.firstOrNull { it.isLocal }?.ready ?: false

    // ---- User-Aktionen --------------------------------------------------

    fun onNameChange(slotId: Int, newName: String) {
        updateSlots(WaitingLobbyLogic.applyNameChange(_state.value.slots, slotId, newName))
    }

    fun onColorChange(slotId: Int, newColor: PlayerColor) {
        updateSlots(WaitingLobbyLogic.applyColorChange(_state.value.slots, slotId, newColor))
    }

    fun onReadyToggle(slotId: Int) {
        // Waehrend der 3-2-1-Countdown laeuft, ignorieren wir Ready-Toggles.
        // Sonst kann der User sein "Bereit" kurz vor dem Spielstart wieder
        // zuruecknehmen, der Server hat aber schon Auto-Start ausgeloest --
        // dann wuerde die App ihn trotzdem ins Spiel navigieren und der
        // User waere irritiert.
        if (_state.value.isCountdownActive) return
        updateSlots(WaitingLobbyLogic.applyReadyToggle(_state.value.slots, slotId))
    }

    // ---- Remote-Updates -------------------------------------------------

    /**
     * Wird vom NetworkSync aufgerufen, wenn der Server eine neue
     * Spielerliste schickt. Aktualisiert die Slots 1..n (nicht den
     * lokalen Slot 0).
     *
     * Wichtig: die Farben der Remote-Spieler werden 1:1 aus dem Server-
     * GameState uebernommen -- damit sieht der Color-Picker in jeder
     * App konsistent welche Farben schon belegt sind, und der User kann
     * keinen Konflikt mehr ausloesen, indem er eine Farbe waehlt, die
     * ein anderer Spieler schon hat.
     */
    fun applyRemoteState(remotePlayers: List<Player>) {
        val currentSlots = _state.value.slots

        var newSlots = currentSlots.mapIndexed { index, slot ->
            if (slot.isLocal) {
                slot
            } else {
                val remoteIndex = index - 1
                val remotePlayer = remotePlayers.getOrNull(remoteIndex)
                if (remotePlayer == null) {
                    slot.copy(status = SlotStatus.Empty, name = "", ready = false)
                } else {
                    slot.copy(
                        status = SlotStatus.Player,
                        name = remotePlayer.name,
                        color = remotePlayer.color,
                        ready = true,
                        isLocal = false
                    )
                }
            }
        }

        // Auto-Reassign: wenn der lokale Slot eine Farbe hat, die ein
        // Remote-Spieler bereits belegt, UND der User noch nicht "ready"
        // geklickt hat, vergeben wir automatisch die naechste freie Farbe.
        // Damit muss der User nicht jedes Mal manuell wechseln, wenn sein
        // RED-Default mit dem RED-Default des Hosts kollidiert.
        // Wenn der User schon bewusst ready geklickt hat, lassen wir seine
        // Wahl unangetastet -- ein Color-Konflikt wird dann vom Server
        // ueber COLOR_ALREADY_TAKEN abgefangen.
        val localSlot = newSlots.firstOrNull { it.isLocal }
        if (localSlot != null && !localSlot.ready) {
            val remoteColors = newSlots
                .filter { !it.isLocal && it.status != SlotStatus.Empty }
                .map { it.color }
                .toSet()
            if (localSlot.color in remoteColors) {
                val freeColor = PlayerColor.entries.firstOrNull { it !in remoteColors }
                if (freeColor != null) {
                    newSlots = newSlots.map { s ->
                        if (s.isLocal) s.copy(color = freeColor) else s
                    }
                }
            }
        }

        updateSlots(newSlots)
    }

    // ---- Fehler-Handling -----------------------------------------------

    /**
     * Setzt den lokalen Slot 0 explizit auf ready=false. Wird vom
     * NetworkSync aufgerufen, wenn der Server den Beitritt mit
     * COLOR_ALREADY_TAKEN abgelehnt hat -- damit ist der Slot wieder
     * editierbar und der User kann eine andere Farbe waehlen.
     */
    fun clearLocalReady() {
        val newSlots = _state.value.slots.map { slot ->
            if (slot.isLocal) slot.copy(ready = false) else slot
        }
        updateSlots(newSlots)
    }

    /**
     * Setzt eine Fehlermeldung, die der Screen als Snackbar anzeigen
     * soll.
     */
    fun showError(message: String) {
        _state.value = _state.value.copy(errorMessage = message)
    }

    /**
     * Wird nach dem Schliessen der Snackbar aufgerufen, damit die
     * gleiche Fehlermeldung nicht erneut auftaucht.
     */
    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    /**
     * Wird vom NetworkSync aufgerufen, sobald der Server den lokalen
     * Spieler in einem GameState-Broadcast bestaetigt. Setzt das
     * joinedServer-Flag, das im UI Name- und Farbwahl sperrt.
     *
     * Idempotent: ein zweiter Aufruf aendert nichts mehr.
     */
    fun markJoinedServer() {
        if (_state.value.joinedServer) return
        _state.value = _state.value.copy(joinedServer = true)
    }

    // ---- Internes -------------------------------------------------------

    private fun updateSlots(newSlots: List<PlayerSlot>) {
        _state.value = _state.value.copy(slots = newSlots)
        maybeStartCountdown()
    }

    private fun maybeStartCountdown() {
        val allReady = WaitingLobbyLogic.allReady(_state.value.slots)
        if (allReady && countdownJob == null) {
            startCountdown()
        } else if (!allReady && countdownJob != null) {
            cancelCountdown()
        }
    }

    private fun startCountdown() {
        countdownJob = viewModelScope.launch {
            var remaining = COUNTDOWN_SECONDS
            _state.value = _state.value.copy(countdown = remaining, countdownComplete = false)
            while (remaining > 0) {
                delay(COUNTDOWN_TICK_MS)
                remaining -= 1
                _state.value = _state.value.copy(countdown = remaining)
            }
            // Countdown durchgelaufen -> Signal an LobbyNetworkSync, dass jetzt
            // zum GameScreen navigiert werden darf.
            _state.value = _state.value.copy(countdownComplete = true)
        }
    }

    private fun cancelCountdown() {
        countdownJob?.cancel()
        countdownJob = null
        _state.value = _state.value.copy(countdown = -1, countdownComplete = false)
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }

    companion object {
        private const val COUNTDOWN_SECONDS = 3
        private const val COUNTDOWN_TICK_MS = 1000L
    }
}
