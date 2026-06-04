package at.aau.serg.websocketbrokerdemo.ui.game.tophud

import androidx.lifecycle.ViewModel
import at.aau.serg.websocketbrokerdemo.network.GameSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

/**
 * ViewModel der Schummel-Geschenk-Mechanik.
 *
 * Trackt den lokalen Klick-Counter, wuerfelt das Delta beim 5. Klick
 * und schickt das Ergebnis an den Server. Verwaltet ausserdem den
 * lokalen "schon entschieden"-Status fuer das Steal-Popup, damit das
 * Popup nach dem Klick sofort verschwindet ohne aufs Server-State zu
 * warten.
 */
class CheatGiftViewModel(
    private val session: GameSession,
    private val random: Random = Random.Default) : ViewModel() {

    private val _state = MutableStateFlow(CheatGiftState())

    val state: StateFlow<CheatGiftState> = _state.asStateFlow()

    /**
     * Wird vom GameScreen aufgerufen wenn das pendingGift im GameState
     * verschwindet (jemand hat geklaut oder alle haben "Nein" gesagt).
     * Reset des hasResponded-Flags fuer den naechsten moeglichen
     * Steal-Vorgang.
     *
     * NOTE: pro Match bekommt jeder Spieler eh nur EIN Geschenk, aber
     * theoretisch kann ein Spieler mehrere Geschenke seiner Mitspieler
     * stehlen wollen. Daher resetten wir hasResponded zwischen den
     * Geschenken.
     */
    fun onPendingGiftCleared() {
        if (_state.value.hasResponded) {
            _state.value = _state.value.copy(hasResponded = false)
        }
    }

    /**
     * Tap aufs Geschenk-Icon im HUD.
     *
     * Inkrementiert den lokalen Counter. Beim 5. Klick wird ein Delta
     * gewuerfelt und an den Server geschickt -- der Server kuemmert sich
     * um Gold-Anpassung und Steal-Broadcast an die anderen Spieler.
     */
    fun onGiftClick(localName: String) {
        val newCount = _state.value.clickCount + 1
        if (newCount >= CheatGiftLogic.CLICKS_TO_TRIGGER) {
            val delta = CheatGiftLogic.rollDelta(random)
            session.endpoint.claimCheatGift(
                roomId = session.activeRoomId.value,
                playerName = localName,
                delta = delta
            )
            _state.value = _state.value.copy(clickCount = 0)
        } else {
            _state.value = _state.value.copy(clickCount = newCount)
        }
    }

    /**
     * Spieler hat im Steal-Popup auf "Ja klauen" gedrueckt.
     */
    fun onStealAccept(localName: String) {
        if (_state.value.hasResponded) return
        _state.value = _state.value.copy(hasResponded = true)
        session.endpoint.respondToCheatGift(
            roomId = session.activeRoomId.value,
            playerName = localName,
            accept = true
        )
    }

    /**
     * Spieler hat im Steal-Popup auf "Nein, lass los" gedrueckt.
     */
    fun onStealDecline(localName: String) {
        if (_state.value.hasResponded) return
        _state.value = _state.value.copy(hasResponded = true)
        session.endpoint.respondToCheatGift(
            roomId = session.activeRoomId.value,
            playerName = localName,
            accept = false
        )
    }
}
