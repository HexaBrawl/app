package at.aau.serg.websocketbrokerdemo.ui.waiting

import at.aau.serg.websocketbrokerdemo.ui.waiting.model.PlayerSlot

/**
 * UI-State der Wartelobby.
 *
 * Vom [WaitingLobbyViewModel] ueber einen StateFlow exponiert. Der
 * [WaitingLobbyScreen] liest ausschliesslich diesen State und ruft
 * Handler-Methoden des ViewModels auf -- keine Logik im Composable.
 *
 *  - roomId     Technische Raum-Id (UUID). Wird intern fuer die
 *               STOMP-Kommunikation gebraucht und i.d.R. NICHT in der UI
 *               angezeigt.
 *  - joinCode   Menschenlesbarer 6-Zeichen-Code. Wird in der Wartelobby
 *               angezeigt und ist der Wert, den der User per Klick
 *               kopieren bzw. seinen Mitspielern weitergeben kann.
 *  - slots             Aktuelle Spieler-Slots (lokal + remote + leer)
 *  - countdown         Sekunden bis zum Spielstart, -1 wenn nicht laeuft
 *  - countdownComplete True, sobald der Countdown auf 0 runtergezaehlt
 *                      hat. Dient als Signal fuer die Navigation zum
 *                      GameScreen -- erst wenn der Countdown wirklich
 *                      durchgelaufen ist, wechselt die App auf das
 *                      Spielfeld. Wird beim Start eines neuen Countdowns
 *                      wieder auf false gesetzt.
 *  - errorMessage      Vom Server gemeldeter Fehler (z.B. "Diese Farbe
 *                      ist bereits vergeben"). Wird vom WaitingLobby-
 *                      Screen als Snackbar angezeigt und nach dem
 *                      Verschwinden ueber clearError() zurueckgesetzt.
 */
data class WaitingLobbyState(
    val roomId: String = "",
    val joinCode: String = "",
    val slots: List<PlayerSlot> = emptyList(),
    val countdown: Int = -1,
    val countdownComplete: Boolean = false,
    val errorMessage: String? = null
) {
    /** True wenn der Countdown gerade laeuft. */
    val isCountdownActive: Boolean
        get() = countdown > 0
}
