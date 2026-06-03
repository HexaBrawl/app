package at.aau.serg.websocketbrokerdemo.ui.game

import at.aau.serg.websocketbrokerdemo.data.serverside.GameUnit

/**
 * UI-State des GameScreens.
 *
 * Wird vom [GameViewModel] ueber einen StateFlow exponiert. Anders als
 * der serverseitige `data.serverside.GameState` ist diese Klasse rein
 * fuer die UI gedacht und enthaelt zusaetzlich die lokale Auswahl der
 * Einheit sowie Debug-Informationen.
 *
 *  - [selected]   Aktuell vom lokalen Spieler ausgewaehlte Einheit
 *                 (null = keine Auswahl)
 *  - [lastTap]    Debug-String: zuletzt angetippte Zelle
 *  - [lastMove]   Debug-String: zuletzt gesendeter Move
 */
data class GameUiState(
    val selected: GameUnit? = null,
    val lastTap: String = "-",
    val lastMove: String = "-"
)
