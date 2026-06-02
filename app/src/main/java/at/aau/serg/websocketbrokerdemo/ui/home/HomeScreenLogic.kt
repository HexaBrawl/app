package at.aau.serg.websocketbrokerdemo.ui.home

import android.app.Activity
import android.content.Context
import androidx.navigation.NavController
import at.aau.serg.websocketbrokerdemo.audio.MusicManager
import at.aau.serg.websocketbrokerdemo.ui.navigation.Screen

/**
 * Bündelt die Logik des HomeScreens.
 *
 * Hält keinen eigenen State -- der HomeScreen ist stateless, deshalb keine
 * ViewModel-Anbindung. Diese Klasse extrahiert die Side-Effects und
 * Navigation-Logik aus dem Composable, damit sie testbar wird.
 *
 * Verantwortungen:
 *  - Menü-Musik starten
 *  - Navigation zum Hauptmenü (mit Fallback)
 *  - App beenden
 */
object HomeScreenLogic {

    /** Startet die Menü-Musik. */
    fun startMenuMusic(context: Context) {
        MusicManager.playMenuMusic(context)
    }

    /**
     * Versucht zur primären Route zu navigieren. Falls diese noch nicht
     * im NavGraph registriert ist (z. B. weil das Hauptmenü-Composable in
     * einem späteren Task hinzukommt), wird der Fallback genutzt.
     */
    fun navigateSafe(navController: NavController, primary: Screen, fallback: Screen) {
        val target = if (hasRoute(navController, primary)) primary else fallback
        navController.navigate(target.route)
    }

    /** Prüft ob ein Screen im NavGraph registriert ist. */
    fun hasRoute(navController: NavController, screen: Screen): Boolean =
        navController.graph.any { it.route == screen.route }

    /**
     * Beendet die App. Wenn der Context keine Activity ist (z. B. Preview-
     * Mode oder Test), passiert nichts -- darf nicht crashen.
     */
    fun exitApp(activity: Activity?) {
        activity?.finish()
    }

    /** Convenience: Klick auf PLAY -> ins Hauptmenü, sonst Fallback ins Spiel. */
    fun onPlayClicked(navController: NavController) {
        navigateSafe(navController, primary = Screen.MainMenu, fallback = Screen.Game)
    }

    /** Convenience: Klick auf Settings. */
    fun onSettingsClicked(navController: NavController) {
        navController.navigate(Screen.Settings.route)
    }
}
