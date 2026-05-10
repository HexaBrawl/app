package at.aau.serg.websocketbrokerdemo.ui

import at.aau.serg.websocketbrokerdemo.ui.navigation.MusicTrack
import at.aau.serg.websocketbrokerdemo.ui.navigation.NavigationLogic
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class NavigationLogicTest {

    @Test
    fun `waiting routes return Tournament`() {
        assertEquals(MusicTrack.Tournament, NavigationLogic.trackForRoute("waiting_dual"))
        assertEquals(MusicTrack.Tournament, NavigationLogic.trackForRoute("waiting_triad"))
        assertEquals(MusicTrack.Tournament, NavigationLogic.trackForRoute("waiting_battlefield"))
    }

    @Test
    fun `game route returns Battle`() {
        assertEquals(MusicTrack.Battle, NavigationLogic.trackForRoute("game"))
    }

    @Test
    fun `all other routes return Menu`() {
        assertEquals(MusicTrack.Menu, NavigationLogic.trackForRoute("home"))
        assertEquals(MusicTrack.Menu, NavigationLogic.trackForRoute("settings"))
        assertEquals(MusicTrack.Menu, NavigationLogic.trackForRoute("mainmenu"))
        assertEquals(MusicTrack.Menu, NavigationLogic.trackForRoute("lobby_dual"))
        assertEquals(MusicTrack.Menu, NavigationLogic.trackForRoute("lobby_triad"))
        assertEquals(MusicTrack.Menu, NavigationLogic.trackForRoute("lobby_battlefield"))
        assertEquals(MusicTrack.Menu, NavigationLogic.trackForRoute(null))
    }
}
