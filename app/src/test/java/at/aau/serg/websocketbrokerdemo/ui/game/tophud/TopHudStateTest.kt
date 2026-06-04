package at.aau.serg.websocketbrokerdemo.ui.game.tophud

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class TopHudStateTest {

    @Test
    fun `default state has popup None`() {
        assertEquals(HudPopup.None, TopHudState().popup)
    }

    @Test
    fun `copy creates independent state`() {
        val original = TopHudState()
        val modified = original.copy(popup = HudPopup.Menu)
        assertEquals(HudPopup.None, original.popup)
        assertEquals(HudPopup.Menu, modified.popup)
    }

    @Test
    fun `HudPopup has four entries`() {
        assertEquals(4, HudPopup.entries.size)
    }

    @Test
    fun `HudPopup contains None Menu Info Settings`() {
        val names = HudPopup.entries.map { it.name }.toSet()
        assertEquals(setOf("None", "Menu", "Info", "Settings"), names)
    }

    @Test
    fun `equality holds for same popup`() {
        assertEquals(TopHudState(HudPopup.Menu), TopHudState(HudPopup.Menu))
        assertNotEquals(TopHudState(HudPopup.Menu), TopHudState(HudPopup.Info))
    }
}