package at.aau.serg.websocketbrokerdemo.ui.navigation

/**
 * Type-safe Repräsentation aller Navigations-Ziele der App.
 *
 * Jeder Screen ist ein eigenes object mit einer route-Property, die als
 * Identifier für Compose Navigation dient. Statt String-Literale wie
 * "home" oder "waiting_dual" über die Codebase zu verteilen, navigiert
 * die App ausschließlich über diese typsicheren Referenzen.
 *
 * Vorteile:
 *  - Tippfehler werden zur Compile-Zeit gefangen (statt erst beim Crash)
 *  - Umbenennen einer Route ändert nur eine Zeile (statt grep'n'replace)
 *  - Vollständigkeit prüfbar (alle Subklassen einer sealed class sind
 *    bekannt -> ScreenTest kann garantieren dass keine vergessen wird)
 *
 * Hinweis zu LOBBY_ und WAITING_:
 *  Es gibt jeweils drei Varianten (Dual/Triad/Battlefield). Aktuell wird
 *  pro Spielmodus ein eigener Composable-Eintrag im NavHost registriert.
 *  Sobald der Network-Layer-Refactor abgeschlossen ist und die Wartelobby
 *  über Session-IDs anstelle von Modi adressiert wird, lassen sich diese
 *  drei Subklassen zu einer parametrisierten zusammenführen.
 */
sealed class Screen(val route: String) {

    /** Start-Bildschirm der App. */
    object Home : Screen("home")

    /** Einstellungen (Sprache, Musik, Sound). */
    object Settings : Screen("settings")

    /** Hauptmenü mit der Karten-Übersicht und den Hotspots. */
    object MainMenu : Screen("mainmenu")

    /** Modus-Lobby für Dual Valley (2 Spieler). */
    object LobbyDual : Screen("lobby_dual")

    /** Modus-Lobby für Triad Outpost (3 Spieler). */
    object LobbyTriad : Screen("lobby_triad")

    /** Modus-Lobby für Battlefield Peaks (4 Spieler). */
    object LobbyBattlefield : Screen("lobby_battlefield")

    /** Wartelobby für Dual Valley. */
    object WaitingDual : Screen("waiting_dual")

    /** Wartelobby für Triad Outpost. */
    object WaitingTriad : Screen("waiting_triad")

    /** Wartelobby für Battlefield Peaks. */
    object WaitingBattlefield : Screen("waiting_battlefield")

    /** Das eigentliche Spiel. */
    object Game : Screen("game")

    companion object {

        /**
         * Alle bekannten Screens.
         *
         * Wird ueber `by lazy` aufgebaut, weil ein direkt initialisierter
         * `listOf(Home, Settings, ...)` zur Companion-Init-Zeit zu Null-
         * Referenzen fuehren wuerde -- die einzelnen Sealed-Class-Objects
         * werden erst nach dem companion object initialisiert, daher
         * muss der Aufbau verzoegert werden.
         */
        val all: List<Screen> by lazy {
            listOf(
                Home, Settings, MainMenu,
                LobbyDual, LobbyTriad, LobbyBattlefield,
                WaitingDual, WaitingTriad, WaitingBattlefield,
                Game
            )
        }

        /**
         * Sucht das Screen-Object mit der gegebenen Route.
         *
         * @return passender Screen oder null wenn keine Route matched
         *         (z. B. bei null-Input oder unbekanntem String).
         */
        fun fromRoute(route: String?): Screen? =
            all.firstOrNull { it.route == route }
    }
}