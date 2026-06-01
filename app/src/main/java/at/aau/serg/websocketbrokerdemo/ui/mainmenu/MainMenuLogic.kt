package at.aau.serg.websocketbrokerdemo.ui.mainmenu

import androidx.navigation.NavController

/**
 * Bündelt Side-Effects des Hauptmenüs.
 *
 * Aktuell nur das Navigieren in die Lobby des gewählten Modus.
 * Wenn später z. B. Analytics dazukommen ("user picked DUAL_VALLEY"),
 * landen die Calls hier -- das ViewModel bleibt schlank.
 */
object MainMenuLogic {

    /** Navigiert in die Lobby des angegebenen Modus. */
    fun navigateToLobby(navController: NavController, mode: GameMode) {
        navController.navigate(mode.route)
    }

    /** Zurück zum Home-Screen (Backstack-Pop). */
    fun navigateBack(navController: NavController) {
        navController.popBackStack()
    }
}
