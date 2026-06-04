package at.aau.serg.websocketbrokerdemo.ui.waiting

import at.aau.serg.websocketbrokerdemo.ui.waiting.model.PlayerSlot

/**
 * UI-State der Wartelobby.
 *
 * Vom [WaitingLobbyViewModel] ueber einen StateFlow exponiert. Der
 * [WaitingLobbyScreen] liest ausschliesslich diesen State und ruft
 * Handler-Methoden des ViewModels auf -- keine Logik im Composable.
 *
 *  - [slots]      Aktuelle Spieler-Slots (lokal + remote + leer)
 *  - [countdown]  Sekunden bis zum Spielstart, -1 wenn nicht laeuft
 */
data class WaitingLobbyState(
    val slots: List<PlayerSlot> = emptyList(),
    val countdown: Int = -1
) {
    /** True wenn der Countdown gerade laeuft. */
    val isCountdownActive: Boolean
        get() = countdown > 0
}