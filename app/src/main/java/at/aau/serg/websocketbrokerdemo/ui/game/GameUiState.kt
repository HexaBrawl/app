package at.aau.serg.websocketbrokerdemo.ui.game

import at.aau.serg.websocketbrokerdemo.data.serverside.GameUnit
import at.aau.serg.websocketbrokerdemo.data.serverside.UnitType

/**
 * UI-State des GameScreens.
 *
 * Wird vom [GameViewModel] ueber einen StateFlow exponiert. Anders als
 * der serverseitige `data.serverside.GameState` ist diese Klasse rein
 * fuer die UI gedacht.
 *
 *  - [selected]       Aktuell ausgewaehlte eigene Einheit (zum Bewegen)
 *  - [placementMode]  Wenn != null: Spieler hat im Bottom-HUD eine
 *                     Truppe zum Kaufen angetippt und wartet jetzt auf
 *                     einen Karten-Tap, um die Einheit zu platzieren.
 *                     Selected und PlacementMode schliessen sich aus.
 */
data class GameUiState(
    val selected: GameUnit? = null,
    val placementMode: UnitType? = null
)
