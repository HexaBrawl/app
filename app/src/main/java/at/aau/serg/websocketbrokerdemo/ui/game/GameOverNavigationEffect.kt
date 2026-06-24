package at.aau.serg.websocketbrokerdemo.ui.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import at.aau.serg.websocketbrokerdemo.data.serverside.GameState
import at.aau.serg.websocketbrokerdemo.data.serverside.GameStatus
import at.aau.serg.websocketbrokerdemo.network.GameSession
import at.aau.serg.websocketbrokerdemo.ui.navigation.Screen

/**
 * Beobachtet den Spielzustand und navigiert beim Spielende auf den
 * passenden EndScreen (Win / Loss).
 *
 * Wird vom [GameScreen] aufgerufen, kapselt aber die komplette
 * Navigations-Logik -- dadurch bleibt GameScreen schlank und Sonar's
 * Cognitive Complexity dort niedrig.
 *
 * Auslöser fürs Game-Over:
 *  - GameStatus.FINISHED (klassisches Spielende, Sieger steht fest)
 *  - Lokaler Spieler ist nicht mehr in state.players enthalten
 *    (Eliminierung im 3-/4-Spieler-Modus; Server entfernt
 *    Ausgeschiedene aus der Liste, siehe GameService.eliminatePlayer).
 *
 * Vor der Navigation wird die Reconnect-Identitaet via
 * sessionRepository.clear() geloescht -- damit ein "App wegswipen"
 * auf dem EndScreen kein /leave fuer einen toten Raum mehr ausloest.
 */
@Composable
fun GameOverNavigationEffect(
    gameState: GameState?,
    status: GameStatus,
    localName: String?,
    session: GameSession,
    navController: NavController
) {
    LaunchedEffect(status, gameState?.players) {
        val state = gameState ?: return@LaunchedEffect
        val name = localName ?: return@LaunchedEffect

        if (!GameOverLogic.isGameOver(state.players, status, name)) return@LaunchedEffect

        session.sessionRepository.clear()
        val route = if (GameOverLogic.isLocalWinner(state.winner, name)) {
            Screen.EndWin.route
        } else {
            Screen.EndLoss.route
        }
        navController.navigate(route) {
            popUpTo(Screen.Home.route) { inclusive = false }
        }
    }
}
