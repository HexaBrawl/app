package at.aau.serg.websocketbrokerdemo.ui.mainmenu

import androidx.navigation.NavController
import at.aau.serg.websocketbrokerdemo.ui.navigation.Screen
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests für MainMenuLogic.
 *
 * Decken die Navigation zur Modus-Lobby (eine pro GameMode), die
 * screenForMode-Mapping-Funktion und den Zurück-Schritt ab.
 */
class MainMenuLogicTest {

    private lateinit var navController: NavController

    @BeforeEach
    fun setUp() {
        navController = mockk(relaxed = true)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    // ---- screenForMode --------------------------------------------------

    @Test
    fun `screenForMode maps DUAL_VALLEY to LobbyDual`() {
        assertEquals(Screen.LobbyDual, MainMenuLogic.screenForMode(GameMode.DUAL_VALLEY))
    }

    @Test
    fun `screenForMode maps TRIAD_OUTPOST to LobbyTriad`() {
        assertEquals(Screen.LobbyTriad, MainMenuLogic.screenForMode(GameMode.TRIAD_OUTPOST))
    }

    @Test
    fun `screenForMode maps BATTLEFIELD_PEAKS to LobbyBattlefield`() {
        assertEquals(Screen.LobbyBattlefield, MainMenuLogic.screenForMode(GameMode.BATTLEFIELD_PEAKS))
    }

    // ---- navigateToLobby ------------------------------------------------

    @Test
    fun `navigateToLobby navigates to LobbyDual for DUAL_VALLEY`() {
        MainMenuLogic.navigateToLobby(navController, GameMode.DUAL_VALLEY)
        verify { navController.navigate(Screen.LobbyDual.route) }
    }

    @Test
    fun `navigateToLobby navigates to LobbyTriad for TRIAD_OUTPOST`() {
        MainMenuLogic.navigateToLobby(navController, GameMode.TRIAD_OUTPOST)
        verify { navController.navigate(Screen.LobbyTriad.route) }
    }

    @Test
    fun `navigateToLobby navigates to LobbyBattlefield for BATTLEFIELD_PEAKS`() {
        MainMenuLogic.navigateToLobby(navController, GameMode.BATTLEFIELD_PEAKS)
        verify { navController.navigate(Screen.LobbyBattlefield.route) }
    }

    // ---- navigateBack ---------------------------------------------------

    @Test
    fun `navigateBack pops the back stack`() {
        MainMenuLogic.navigateBack(navController)
        verify { navController.popBackStack() }
    }
}
