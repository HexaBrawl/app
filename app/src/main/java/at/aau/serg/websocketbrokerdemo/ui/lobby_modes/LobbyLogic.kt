package at.aau.serg.websocketbrokerdemo.ui.lobby_modes

import at.aau.serg.websocketbrokerdemo.ui.mainmenu.GameMode
import at.aau.serg.websocketbrokerdemo.ui.navigation.Screen

/**
 * Navigation-Mapping fuer die Modus-Lobby.
 *
 * Liefert den passenden Wartelobby-[Screen] zum gegebenen [GameMode].
 * Der LobbyScreen nutzt das beim Tap auf "Privates Spiel" / "Mit Code
 * beitreten" / "Zufaelliges Spiel".
 *
 * Diese Klasse ist die einzige Stelle, an der die Verbindung GameMode
 * -> Wartelobby-Screen entschieden wird -- ein zentraler Punkt fuer
 * Erweiterungen (z. B. weitere Modi).
 */
object LobbyLogic {

    /**
     * Liefert den Wartelobby-Screen fuer den gegebenen Spielmodus.
     */
    fun toWaitingScreen(mode: GameMode): Screen =
        when (mode) {
            GameMode.DUAL_VALLEY -> Screen.WaitingDual
            GameMode.TRIAD_OUTPOST -> Screen.WaitingTriad
            GameMode.BATTLEFIELD_PEAKS -> Screen.WaitingBattlefield
        }
}
