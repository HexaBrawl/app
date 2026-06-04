package at.aau.serg.websocketbrokerdemo.ui.game.tophud

/**
 * Welches Popup ueber dem Top-HUD gerade sichtbar ist.
 */
enum class HudPopup {
    /** Kein Popup offen. */
    None,

    /** Menue-Popup mit Settings/Info-Auswahl. */
    Menu,

    /** Info-Popup mit Spielerklaerung inkl. Schummel-Hinweis. */
    Info,

    /** In-Game-Einstellungen (nur Audio, ohne Sprache). */
    Settings
}

/**
 * UI-State des Top-HUDs.
 *
 * Verwaltet die Sichtbarkeit der Popups. Die Spielzustands-Daten
 * (Gold, Income) werden direkt vom GameState abgelesen.
 */
data class TopHudState(
    val popup: HudPopup = HudPopup.None
)
