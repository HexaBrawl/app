package at.aau.serg.websocketbrokerdemo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import at.aau.serg.websocketbrokerdemo.ui.lobby.HomeScreen
import at.aau.serg.websocketbrokerdemo.ui.game.GameScreen
import MyStomp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import at.aau.serg.websocketbrokerdemo.audio.MusicManager
import at.aau.serg.websocketbrokerdemo.ui.lobby_modes.LobbyScreen
import at.aau.serg.websocketbrokerdemo.ui.mainmenu.GameMode
import at.aau.serg.websocketbrokerdemo.ui.mainmenu.MainMenuScreen
import at.aau.serg.websocketbrokerdemo.ui.settings.SettingsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    myStomp: MyStomp,
    responseState: State<String>
) {
    NavHost(navController, startDestination = "home") {

        composable("home") {
            HomeScreen(navController)
        }

        composable("settings") {
            SettingsScreen(navController)
        }

        composable("mainmenu") {
            MainMenuScreen(navController)
        }

        // Drei Lobbys – jeweils eine eigene Route, damit der Backstack
        // sauber ist (zurück geht direkt zum Hauptmenü).
        composable(GameMode.DUAL_VALLEY.route) {
            LobbyScreen(GameMode.DUAL_VALLEY, navController)
        }
        composable(GameMode.TRIAD_OUTPOST.route) {
            LobbyScreen(GameMode.TRIAD_OUTPOST, navController)
        }
        composable(GameMode.BATTLEFIELD_PEAKS.route) {
            LobbyScreen(GameMode.BATTLEFIELD_PEAKS, navController)
        }

        composable("game") {
            LaunchedEffect(Unit) {
                MusicManager.pause()
            }
            GameScreen(myStomp, responseState)
        }
    }
}