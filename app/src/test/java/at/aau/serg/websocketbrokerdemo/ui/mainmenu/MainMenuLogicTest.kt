package at.aau.serg.websocketbrokerdemo.ui.mainmenu

import androidx.navigation.NavController
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests für MainMenuLogic.
 *
 * Reine Forwarding-Tests: NavController-Aufrufe werden mit MockK abgefangen
 * und überprüft. Keine eigene Logik im Object, daher dünne Tests.
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

    @Test
    fun `navigateToLobby uses the routes of DUAL_VALLEY mode`() {
        MainMenuLogic.navigateToLobby(navController, GameMode.DUAL_VALLEY)
        verify { navController.navigate("lobby_dual") }
    }

    @Test
    fun `navigateToLobby uses the route of TRIAD_OUTPOST mode`() {
        MainMenuLogic.navigateToLobby(navController, GameMode.TRIAD_OUTPOST)
        verify { navController.navigate("lobby_triad") }
    }

    @Test
    fun `navigateToLobby uses the route of BATTLEFIELD_PEAKS mode`() {
        MainMenuLogic.navigateToLobby(navController, GameMode.BATTLEFIELD_PEAKS)
        verify { navController.navigate("lobby_battlefield") }
    }

    @Test
    fun `navigateBack pops the back stack`() {
        MainMenuLogic.navigateBack(navController)
        verify { navController.popBackStack() }
    }
}
