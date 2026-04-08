package at.aau.serg.websocketbrokerdemo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import at.aau.serg.websocketbrokerdemo.ui.lobby.HomeScreen
import at.aau.serg.websocketbrokerdemo.ui.game.GameScreen
import MyStomp
import androidx.compose.runtime.State

@Composable
fun AppNavHost(navController: NavHostController, myStomp: MyStomp, responseState: State<String>) {
    NavHost(navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("game") { GameScreen(myStomp, responseState) }
    }
}