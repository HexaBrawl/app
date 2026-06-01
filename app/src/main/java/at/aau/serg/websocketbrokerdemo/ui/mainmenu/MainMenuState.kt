package at.aau.serg.websocketbrokerdemo.ui.mainmenu

/**
 * UI-State des Hauptmenüs.
 *
 * Wird vom ViewModel als StateFlow exponiert und vom MainMenuScreen
 * beobachtet. Nur die UI-relevanten Flags -- keine NavController-
 * Referenzen oder Funktionen.
 *
 * @param pendingMode Der vom Spieler ausgewählte Modus, für den noch die
 *                    Bestätigung ansteht. `null` = kein Popup aktiv.
 * @param showInfo    Ob der Spielregel-Dialog angezeigt wird.
 */
data class MainMenuState(
    val pendingMode: GameMode? = null,
    val showInfo: Boolean = false
)
