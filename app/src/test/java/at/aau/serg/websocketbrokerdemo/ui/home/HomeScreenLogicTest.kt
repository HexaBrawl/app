package at.aau.serg.websocketbrokerdemo.ui.home

import android.app.Activity
import android.content.Context
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import at.aau.serg.websocketbrokerdemo.audio.MusicManager
import at.aau.serg.websocketbrokerdemo.ui.home.HomeScreenLogic
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests für HomeScreenLogic.
 *
 * HomeScreenLogic ist ein object ohne eigenen State -- jeder Aufruf
 * delegiert direkt an MusicManager bzw. NavController. Wir testen vor allem:
 *
 *  - Routing-Verzweigungen (hasRoute, navigateSafe mit Fallback)
 *  - Korrekte Forwards an die Singletons / NavController
 *  - Null-Safety bei der Activity (exitApp)
 *
 * Mocking:
 *  - NavController + NavGraph -> normale Mocks. Den NavGraph müssen wir
 *    sorgfältig nachbilden, weil hasRoute() darüber iteriert.
 *  - MusicManager ist ein object -> mockkObject
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

    // -------------------------------------------------------------------------
    // startMenuMusic()
    // -------------------------------------------------------------------------

    @Test
    fun `startMenuMusic delegates to MusicManager`() {
        // Reines Forwarding: HomeScreen.LaunchedEffect ruft das auf, hier
        // prüfen wir nur dass der MusicManager wirklich angestoßen wird.
        val context = mockk<Context>(relaxed = true)

        HomeScreenLogic.startMenuMusic(context)

        verify { MusicManager.playMenuMusic(context) }
    }

    // -------------------------------------------------------------------------
    // hasRoute()
    // -------------------------------------------------------------------------

    @Test
    fun `hasRoute returns true when route exists in graph`() {
        // hasRoute() iteriert über den NavGraph und sucht nach destination.route.
        // Wir bauen einen NavGraph mit einem Eintrag und prüfen das Matching.
        val destination = mockk<NavDestination>()
        every { destination.route } returns "settings"
        every { navGraph.iterator() } returns mutableListOf(destination).iterator()

        assertTrue(HomeScreenLogic.hasRoute(navController, "settings"))
    }

    @Test
    fun `hasRoute returns false when route is missing`() {
        // Edge-Case: NavGraph enthält andere Routen, aber nicht die gesuchte.
        val destination = mockk<NavDestination>()
        every { destination.route } returns "home"
        every { navGraph.iterator() } returns mutableListOf(destination).iterator()

        assertFalse(HomeScreenLogic.hasRoute(navController, "settings"))
    }

    @Test
    fun `hasRoute returns false for empty navigation graph`() {
        // Robustheit: leerer Graph darf nicht crashen.
        every { navGraph.iterator() } returns mutableListOf<NavDestination>().iterator()

        assertFalse(HomeScreenLogic.hasRoute(navController, "anything"))
    }

    // -------------------------------------------------------------------------
    // navigateSafe()
    // -------------------------------------------------------------------------

    @Test
    fun `navigateSafe uses primary route when it exists`() {
        // Happy-Path: primary ist im NavGraph registriert -> dahin navigieren.
        val destination = mockk<NavDestination>()
        every { destination.route } returns "mainmenu"
        every { navGraph.iterator() } returns mutableListOf(destination).iterator()

        HomeScreenLogic.navigateSafe(navController, primary = "mainmenu", fallback = "game")

        verify { navController.navigate("mainmenu") }
        verify(exactly = 0) { navController.navigate("game") }
    }

    @Test
    fun `navigateSafe falls back when primary route is missing`() {
        // Während der Entwicklung kann das Hauptmenü-Composable noch fehlen --
        // dann soll der Fallback (z. B. direkt "game") greifen, damit der
        // PLAY-Button nicht crasht.
        val destination = mockk<NavDestination>()
        every { destination.route } returns "game"  // nur "game" registriert
        every { navGraph.iterator() } returns mutableListOf(destination).iterator()

        HomeScreenLogic.navigateSafe(navController, primary = "mainmenu", fallback = "game")

        verify { navController.navigate("game") }
        verify(exactly = 0) { navController.navigate("mainmenu") }
    }

    // -------------------------------------------------------------------------
    // exitApp()
    // -------------------------------------------------------------------------

    @Test
    fun `exitApp calls finish on the activity`() {
        // Normalfall: User drückt Exit -> Activity wird beendet.
        val activity = mockk<Activity>(relaxed = true)
        justRun { activity.finish() }

        HomeScreenLogic.exitApp(activity)

        verify { activity.finish() }
    }

    @Test
    fun `exitApp handles null activity gracefully`() {
        // Wichtig: in Compose-Previews oder Tests ist der LocalContext
        // manchmal keine Activity -> activity ist null. Darf NICHT crashen.
        HomeScreenLogic.exitApp(null)
        // Kein verify -- der Test prüft nur dass keine Exception fliegt.
    }

    // -------------------------------------------------------------------------
    // Convenience: onPlayClicked / onSettingsClicked
    // -------------------------------------------------------------------------

    @Test
    fun `onPlayClicked navigates to mainmenu when available`() {
        val destination = mockk<NavDestination>()
        every { destination.route } returns "mainmenu"
        every { navGraph.iterator() } returns mutableListOf(destination).iterator()

        HomeScreenLogic.onPlayClicked(navController)

        verify { navController.navigate("mainmenu") }
    }

    @Test
    fun `onPlayClicked falls back to game when mainmenu missing`() {
        // Dokumentiert den Fallback-Pfad für den PLAY-Button:
        // Wenn das Hauptmenü-Composable noch nicht da ist, springt der Spieler
        // direkt ins Spiel statt zu crashen.
        val destination = mockk<NavDestination>()
        every { destination.route } returns "game"
        every { navGraph.iterator() } returns mutableListOf(destination).iterator()

        HomeScreenLogic.onPlayClicked(navController)

        verify { navController.navigate("game") }
    }

    @Test
    fun `onSettingsClicked navigates to settings route`() {
        // Settings-Route existiert immer (kein Fallback nötig), daher
        // direkter navigate-Aufruf.
        HomeScreenLogic.onSettingsClicked(navController)

        verify { navController.navigate("settings") }
    }
}