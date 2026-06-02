package at.aau.serg.websocketbrokerdemo.ui.home

import android.app.Activity
import android.content.Context
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import at.aau.serg.websocketbrokerdemo.audio.MusicManager
import at.aau.serg.websocketbrokerdemo.ui.navigation.Screen
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests für HomeScreenLogic.
 *
 * Forwarding-Tests gegen MusicManager und NavController. Der NavGraph
 * wird in jedem Test minimal mit den nötigen Destinationen bestückt,
 * um die Verzweigung "Route vorhanden / fehlt" sauber abdecken zu können.
 */
class HomeScreenLogicTest {

    private lateinit var navController: NavController
    private lateinit var navGraph: NavGraph

    @BeforeEach
    fun setUp() {
        mockkObject(MusicManager)
        navController = mockk(relaxed = true)
        navGraph = mockk(relaxed = true)
        every { navController.graph } returns navGraph
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `startMenuMusic delegates to MusicManager`() {
        val context = mockk<Context>(relaxed = true)

        HomeScreenLogic.startMenuMusic(context)

        verify { MusicManager.playMenuMusic(context) }
    }

    @Test
    fun `hasRoute returns true when screen is registered`() {
        val destination = mockk<NavDestination>()
        every { destination.route } returns Screen.Settings.route
        every { navGraph.iterator() } returns mutableListOf(destination).iterator()

        assertTrue(HomeScreenLogic.hasRoute(navController, Screen.Settings))
    }

    @Test
    fun `hasRoute returns false when screen is missing`() {
        val destination = mockk<NavDestination>()
        every { destination.route } returns Screen.Home.route
        every { navGraph.iterator() } returns mutableListOf(destination).iterator()

        assertFalse(HomeScreenLogic.hasRoute(navController, Screen.Settings))
    }

    @Test
    fun `hasRoute returns false for empty navigation graph`() {
        every { navGraph.iterator() } returns mutableListOf<NavDestination>().iterator()

        assertFalse(HomeScreenLogic.hasRoute(navController, Screen.Home))
    }

    @Test
    fun `navigateSafe navigates to primary when available`() {
        val destination = mockk<NavDestination>()
        every { destination.route } returns Screen.MainMenu.route
        every { navGraph.iterator() } returns mutableListOf(destination).iterator()

        HomeScreenLogic.navigateSafe(navController, primary = Screen.MainMenu, fallback = Screen.Game)

        verify { navController.navigate(Screen.MainMenu.route) }
        verify(exactly = 0) { navController.navigate(Screen.Game.route) }
    }

    @Test
    fun `navigateSafe falls back when primary is missing`() {
        val destination = mockk<NavDestination>()
        every { destination.route } returns Screen.Game.route
        every { navGraph.iterator() } returns mutableListOf(destination).iterator()

        HomeScreenLogic.navigateSafe(navController, primary = Screen.MainMenu, fallback = Screen.Game)

        verify { navController.navigate(Screen.Game.route) }
        verify(exactly = 0) { navController.navigate(Screen.MainMenu.route) }
    }

    @Test
    fun `exitApp calls finish on activity`() {
        val activity = mockk<Activity>(relaxed = true)
        justRun { activity.finish() }

        HomeScreenLogic.exitApp(activity)

        verify { activity.finish() }
    }

    @Test
    fun `exitApp handles null activity gracefully`() {
        HomeScreenLogic.exitApp(null)
        // Kein verify -- der Test prüft nur dass keine Exception fliegt.
    }

    @Test
    fun `onPlayClicked navigates to MainMenu when available`() {
        val destination = mockk<NavDestination>()
        every { destination.route } returns Screen.MainMenu.route
        every { navGraph.iterator() } returns mutableListOf(destination).iterator()

        HomeScreenLogic.onPlayClicked(navController)

        verify { navController.navigate(Screen.MainMenu.route) }
    }

    @Test
    fun `onPlayClicked falls back to Game when MainMenu missing`() {
        val destination = mockk<NavDestination>()
        every { destination.route } returns Screen.Game.route
        every { navGraph.iterator() } returns mutableListOf(destination).iterator()

        HomeScreenLogic.onPlayClicked(navController)

        verify { navController.navigate(Screen.Game.route) }
    }

    @Test
    fun `onSettingsClicked navigates to Settings`() {
        HomeScreenLogic.onSettingsClicked(navController)

        verify { navController.navigate(Screen.Settings.route) }
    }
}
