package at.aau.serg.websocketbrokerdemo.ui.mainmenu

import androidx.navigation.NavController
import at.aau.serg.websocketbrokerdemo.ui.navigation.Screen

/**
 * Bündelt Side-Effects des Hauptmenüs.
 *
 * Aktuell nur das Navigieren in die Lobby des gewählten Modus und
 * der Zurück-Schritt. Wenn später z. B. Analytics dazukommen
 * ("user picked DUAL_VALLEY"), landen die Calls hier -- das ViewModel
 * bleibt schlank.
 */
object MainMenuLogic {

    /**
     * Navigiert in die Modus-spezifische Lobby.
     *
     * Die konkrete Route wird über [screenForMode] aus dem [GameMode]
     * abgeleitet, sodass im Aufruf keine String-Literale auftauchen.
     */
    fun navigateToLobby(navController: NavController, mode: GameMode) {
        navController.navigate(screenForMode(mode).route)
    }

    /** Zurück zum Home-Screen (Backstack-Pop). */
    fun navigateBack(navController: NavController) {
        navController.popBackStack()
    }

    /**
     * Mapping GameMode -> zugehöriger Lobby-Screen.
     *
     * Public-internal damit sie aus Tests prüfbar ist. Wenn in Zukunft
     * weitere Modi dazukommen, ist die Erweiterung an genau dieser
     * Stelle nötig (kein verteiltes String-Suchen).
     */
    fun screenForMode(mode: GameMode): Screen = when (mode) {
        GameMode.DUAL_VALLEY -> Screen.LobbyDual
        GameMode.TRIAD_OUTPOST -> Screen.LobbyTriad
        GameMode.BATTLEFIELD_PEAKS -> Screen.LobbyBattlefield
    }
}
