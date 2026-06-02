package at.aau.serg.websocketbrokerdemo.ui.lobby_modes

import at.aau.serg.websocketbrokerdemo.ui.mainmenu.GameMode

/**
 * Navigation-Mapping fuer die Modus-Lobby.
 *
 * Aktuell ein duenner Wrapper, der den [GameMode] zur passenden
 * Wartelobby-Route uebersetzt. Wird vom [LobbyScreen] beim Tap auf
 * "Privates Spiel" / "Zufaelliges Spiel" / "Beitreten" benutzt.
 *
 * Hinweis: Die String-Routen werden in einem Folge-PR durch das
 * type-safe Screen-Konstrukt ersetzt, sobald der Network-Layer-
 * Refactor abgeschlossen ist.
 */
object LobbyLogic {

    /**
     * Liefert die Wartelobby-Route fuer den gegebenen Spielmodus.
     */
    fun toWaitingRoute(mode: GameMode): String =
        when (mode) {
            GameMode.DUAL_VALLEY -> "waiting_dual"
            GameMode.TRIAD_OUTPOST -> "waiting_triad"
            GameMode.BATTLEFIELD_PEAKS -> "waiting_battlefield"
        }
}
