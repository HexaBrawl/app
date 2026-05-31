package at.aau.serg.websocketbrokerdemo.ui.home

import android.app.Activity
import android.content.Context
import androidx.navigation.NavController
import at.aau.serg.websocketbrokerdemo.audio.MusicManager

/**
 * Bündelt die Logik des HomeScreens.
 *
 * Hält keinen eigenen State -- der HomeScreen ist stateless, deshalb keine
 * ViewModel-Anbindung. Diese Klasse extrahiert nur die Side-Effects und
 * Navigation-Logik aus dem Composable, damit sie testbar wird.
 *
 * Verantwortungen:
 *  - Menü-Musik starten
 *  - Sichere Navigation zum Hauptmenü (mit Fallback)
 *  - App beenden
 */
object HomeScreenLogic {

    /** Startet die Menü-Musik. */
    fun startMenuMusic(context: Context) {
        MusicManager.playMenuMusic(context)
    }

    /**
     * Versucht zur primären Route zu navigieren. Falls die noch nicht im
     * NavGraph registriert ist (z. B. weil das Hauptmenü-Composable in
     * einem späteren Task hinzukommt), wird der Fallback genutzt.
     *
     * Zentrale Stelle für diese Logik -- vorher war sie als private fun
     * im HomeScreen-File und nicht testbar.
     */
    fun navigateSafe(navController: NavController, primary: String, fallback: String) {
        val target = if (hasRoute(navController, primary)) primary else fallback
        navController.navigate(target)
    }

    /** Prüft ob eine Route im NavGraph existiert. */
    fun hasRoute(navController: NavController, route: String): Boolean =
        navController.graph.any { it.route == route }

    /**
     * Beendet die App. Wenn der Context keine Activity ist (z. B. Preview-
     * Mode oder Test), passiert nichts -- darf nicht crashen.
     */
    fun exitApp(activity: Activity?) {
        activity?.finish()
    }

    /**
     * Convenience: Klick auf PLAY -> ins Hauptmenü, sonst Fallback auf "game".
     */
    fun onPlayClicked(navController: NavController) {
        navigateSafe(navController, primary = "mainmenu", fallback = "game")
    }

    /** Convenience: Klick auf Settings. */
    fun onSettingsClicked(navController: NavController) {
        navController.navigate("settings")
    }
}