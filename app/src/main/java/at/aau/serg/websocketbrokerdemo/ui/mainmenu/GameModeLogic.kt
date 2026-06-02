package at.aau.serg.websocketbrokerdemo.ui.mainmenu

/**
 * Helfer-Klasse für Lookups rund um den [GameMode]-Enum.
 *
 * Vorher saß `fromRoute()` als `companion object` direkt im Enum -- das
 * macht den Enum unnötig fett und schlecht testbar (Enums sind im Kotlin-
 * Compiler-Output ein bisschen Magic). Mit der Extraktion ist:
 *  - Der Enum schlank
 *  - Die Lookup-Logik in einem testbaren `object`
 *  - Erweiterbar für künftige Lookups (z. B. byPlayerCount, byBackground)
 */
object GameModeLogic {

    /**
     * Sucht den GameMode anhand einer Navigation-Route.
     *
     * @return passender Modus oder `null` wenn keine Route matched (z. B.
     *         für "home" oder "settings").
     */
    fun fromRoute(route: String?): GameMode? =
        GameMode.entries.firstOrNull { it.route == route }

    /** Lookup nach Spieleranzahl (kleines Bonus-Helfer für Tests/Tools). */
    fun byPlayerCount(count: Int): GameMode? =
        GameMode.entries.firstOrNull { it.playerCount == count }
}
