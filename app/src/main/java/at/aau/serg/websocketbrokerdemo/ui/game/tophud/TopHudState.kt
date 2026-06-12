package at.aau.serg.websocketbrokerdemo.ui.game.tophud

/**
 * UI-State des Top-HUDs.
 *
 * Verwaltet die Sichtbarkeit der Popups. Die Spielzustands-Daten
 * (Gold, Income) werden direkt vom GameState abgelesen.
 */
data class TopHudState(
    val popup: HudPopup = HudPopup.None
)
