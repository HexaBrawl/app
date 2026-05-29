package at.aau.serg.websocketbrokerdemo.ui.navigation

import MyStomp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import at.aau.serg.websocketbrokerdemo.audio.MusicManager
import at.aau.serg.websocketbrokerdemo.ui.game.GameScreen
import at.aau.serg.websocketbrokerdemo.ui.lobby.HomeScreen
import at.aau.serg.websocketbrokerdemo.ui.lobby_modes.LobbyScreen
import at.aau.serg.websocketbrokerdemo.ui.mainmenu.GameMode
import at.aau.serg.websocketbrokerdemo.ui.mainmenu.MainMenuScreen
import at.aau.serg.websocketbrokerdemo.ui.settings.SettingsScreen
import at.aau.serg.websocketbrokerdemo.ui.waiting.WaitingLobbyScreen

/**
 * Welcher Track gehört zu welcher Route.
 *
 * - waiting_*  -> Turnier-Track
 * - game       -> Kampf-Track
 * - sonst      -> Menü-Track (Home, MainMenu, Mode-Lobbys, Settings)
 *
 * So passt sich die Musik automatisch an, egal über welchen Weg der
 * Spieler den Screen erreicht (z. B. Zurück-Button aus der Wartelobby
 * zur Mode-Lobby -> Menü-Track läuft sofort wieder).
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    myStomp: MyStomp,
    responseState: State<String>
) {
    val context = LocalContext.current

    // Aktive Route beobachten und Musik beim Wechsel umstellen.
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    LaunchedEffect(currentRoute) {
        when (NavigationLogic.trackForRoute(currentRoute)) {
            MusicTrack.Menu -> MusicManager.playMenuMusic(context)
            MusicTrack.Tournament -> MusicManager.playTournamentMusic(context)
            MusicTrack.Battle -> MusicManager.playBattleMusic(context)
        }
    }

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

        composable(GameMode.DUAL_VALLEY.route) {
            LobbyScreen(GameMode.DUAL_VALLEY, navController)
        }
        composable(GameMode.TRIAD_OUTPOST.route) {
            LobbyScreen(GameMode.TRIAD_OUTPOST, navController)
        }
        composable(GameMode.BATTLEFIELD_PEAKS.route) {
            LobbyScreen(GameMode.BATTLEFIELD_PEAKS, navController)
        }

        composable("waiting_dual") {
            WaitingLobbyScreen(GameMode.DUAL_VALLEY, navController)
        }
        composable("waiting_triad") {
            WaitingLobbyScreen(GameMode.TRIAD_OUTPOST, navController)
        }
        composable("waiting_battlefield") {
            WaitingLobbyScreen(GameMode.BATTLEFIELD_PEAKS, navController)
        }

        composable("game") {
            GameScreen(myStomp, responseState)
        }
    }
}