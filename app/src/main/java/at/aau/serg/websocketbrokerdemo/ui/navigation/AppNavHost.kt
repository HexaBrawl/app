package at.aau.serg.websocketbrokerdemo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import at.aau.serg.websocketbrokerdemo.audio.MusicManager
import at.aau.serg.websocketbrokerdemo.network.GameSession
import at.aau.serg.websocketbrokerdemo.ui.game.GameScreen
import at.aau.serg.websocketbrokerdemo.ui.home.HomeScreen
import at.aau.serg.websocketbrokerdemo.ui.lobby_modes.LobbyScreen
import at.aau.serg.websocketbrokerdemo.ui.mainmenu.GameMode
import at.aau.serg.websocketbrokerdemo.ui.mainmenu.MainMenuScreen
import at.aau.serg.websocketbrokerdemo.ui.settings.SettingsScreen
import at.aau.serg.websocketbrokerdemo.ui.waiting.WaitingLobbyScreen

/**
 * Hauptnavigation der App.
 *
 * Registriert fuer jeden [Screen] genau einen Composable-Eintrag und
 * beobachtet zusaetzlich die aktive Route, um beim Wechsel automatisch
 * die passende Hintergrundmusik anzustossen.
 *
 * Saemtliche Routen werden ueber das [Screen]-sealed-class adressiert --
 * String-Literale fuer Routen sind ausschliesslich in [Screen] erlaubt.
 * Die "Route -> Musik-Track"-Logik liegt in [NavigationLogic] und ist
 * dort unit-getestet.
 *
 * Der GameScreen erhaelt aktuell hartkodiert DUAL_VALLEY als Modus,
 * weil der Server noch nicht zurueckliefert, in welchem Modus wir
 * sind. Sobald das Backend ein "currentMode" im GameState mitschickt,
 * koennen wir das hier auflesen statt zu raten.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    session: GameSession
) {
    val context = LocalContext.current

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    LaunchedEffect(currentRoute) {
        when (NavigationLogic.trackForRoute(currentRoute)) {
            MusicTrack.Menu -> MusicManager.playMenuMusic(context)
            MusicTrack.Tournament -> MusicManager.playTournamentMusic(context)
            MusicTrack.Battle -> MusicManager.playBattleMusic(context)
        }
    }

    NavHost(navController, startDestination = Screen.Home.route) {

        composable(Screen.Home.route) {
            HomeScreen(navController)
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }

        composable(Screen.MainMenu.route) {
            MainMenuScreen(navController)
        }

        composable(Screen.LobbyDual.route) {
            LobbyScreen(GameMode.DUAL_VALLEY, navController)
        }
        composable(Screen.LobbyTriad.route) {
            LobbyScreen(GameMode.TRIAD_OUTPOST, navController)
        }
        composable(Screen.LobbyBattlefield.route) {
            LobbyScreen(GameMode.BATTLEFIELD_PEAKS, navController)
        }

        composable(Screen.WaitingDual.route) {
            WaitingLobbyScreen(GameMode.DUAL_VALLEY, navController, session)
        }
        composable(Screen.WaitingTriad.route) {
            WaitingLobbyScreen(GameMode.TRIAD_OUTPOST, navController, session)
        }
        composable(Screen.WaitingBattlefield.route) {
            WaitingLobbyScreen(GameMode.BATTLEFIELD_PEAKS, navController, session)
        }

        composable(Screen.Game.route) {
            // TODO: aktuell hartcodiert auf DUAL_VALLEY. Sobald der
            // GameState einen aktuellen Modus mitliefert oder die
            // Route den Modus als Argument transportiert, kann das
            // hier dynamisch werden.
            GameScreen(session = session, mode = GameMode.DUAL_VALLEY)
        }
    }
}