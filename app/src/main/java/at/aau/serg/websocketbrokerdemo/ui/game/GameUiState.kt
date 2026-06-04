package at.aau.serg.websocketbrokerdemo.ui.game

import at.aau.serg.websocketbrokerdemo.data.serverside.GameUnit

/**
 * UI-State des GameScreens.
 *
 * Wird vom [GameViewModel] ueber einen StateFlow exponiert. Anders als
 * der serverseitige `data.serverside.GameState` ist diese Klasse rein
 * fuer die UI gedacht.
 *
 *  - [selected]   Aktuell vom lokalen Spieler ausgewaehlte Einheit
 *                 (null = keine Auswahl)
 */
data class GameUiState(
    val selected: GameUnit? = null
)
