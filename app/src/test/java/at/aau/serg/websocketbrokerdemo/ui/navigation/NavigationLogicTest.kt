package at.aau.serg.websocketbrokerdemo.ui.navigation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Tests für NavigationLogic.trackForRoute().
 *
 * Decken jede Screen-Variante explizit ab und stellen außerdem das
 * defensive Verhalten bei unbekannten/null-Routen sicher.
 */
class NavigationLogicTest {

    @Test
    fun `trackForRoute returns Tournament for WaitingDual`() {
        assertEquals(MusicTrack.Tournament, NavigationLogic.trackForRoute(Screen.WaitingDual.route))
    }

    @Test
    fun `trackForRoute returns Tournament for WaitingTriad`() {
        assertEquals(MusicTrack.Tournament, NavigationLogic.trackForRoute(Screen.WaitingTriad.route))
    }

    @Test
    fun `trackForRoute returns Tournament for WaitingBattlefield`() {
        assertEquals(MusicTrack.Tournament, NavigationLogic.trackForRoute(Screen.WaitingBattlefield.route))
    }

    @Test
    fun `trackForRoute returns Battle for Game`() {
        assertEquals(MusicTrack.Battle, NavigationLogic.trackForRoute(Screen.Game.route))
    }

    @Test
    fun `trackForRoute returns Menu for Home`() {
        assertEquals(MusicTrack.Menu, NavigationLogic.trackForRoute(Screen.Home.route))
    }

    @Test
    fun `trackForRoute returns Menu for Settings`() {
        assertEquals(MusicTrack.Menu, NavigationLogic.trackForRoute(Screen.Settings.route))
    }

    @Test
    fun `trackForRoute returns Menu for MainMenu`() {
        assertEquals(MusicTrack.Menu, NavigationLogic.trackForRoute(Screen.MainMenu.route))
    }

    @Test
    fun `trackForRoute returns Menu for LobbyDual`() {
        assertEquals(MusicTrack.Menu, NavigationLogic.trackForRoute(Screen.LobbyDual.route))
    }

    @Test
    fun `trackForRoute returns Menu for LobbyTriad`() {
        assertEquals(MusicTrack.Menu, NavigationLogic.trackForRoute(Screen.LobbyTriad.route))
    }

    @Test
    fun `trackForRoute returns Menu for LobbyBattlefield`() {
        assertEquals(MusicTrack.Menu, NavigationLogic.trackForRoute(Screen.LobbyBattlefield.route))
    }

    @Test
    fun `trackForRoute returns Menu as default for unknown route`() {
        // Defensiv: lieber den Menü-Track fallen lassen als die Musik
        // komplett stehen zu lassen, falls jemand mal eine neue Route
        // einführt ohne sie hier zu klassifizieren.
        assertEquals(MusicTrack.Menu, NavigationLogic.trackForRoute("does_not_exist"))
    }

    @Test
    fun `trackForRoute returns Menu for null input`() {
        // currentBackStackEntry liefert nach App-Start zunächst null --
        // darf nicht crashen.
        assertEquals(MusicTrack.Menu, NavigationLogic.trackForRoute(null))
    }

    @Test
    fun `every Screen maps to exactly one track`() {
        // Sanity-Check: für jeden bekannten Screen muss trackForRoute
        // einen Track zurückgeben (keine null/keine Exception).
        Screen.all.forEach { screen ->
            val track = NavigationLogic.trackForRoute(screen.route)
            assert(track in MusicTrack.entries) {
                "Screen ${screen::class.simpleName} hat keinen MusicTrack-Mapping"
            }
        }
    }
}
