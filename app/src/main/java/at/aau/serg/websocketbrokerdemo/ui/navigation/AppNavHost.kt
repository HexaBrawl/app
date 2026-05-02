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

        // TODO Task 2+: composable("mainmenu") { MainMenuScreen(navController) }

        composable("game") {
            // Menü-Track pausieren, sobald wir ins Kampfsystem wechseln.
            // Eine eigene Kampf-Musik kommt in einem späteren Task hierher.
            LaunchedEffect(Unit) {
                MusicManager.pause()
            }
            GameScreen(myStomp, responseState)
        }
    }
}