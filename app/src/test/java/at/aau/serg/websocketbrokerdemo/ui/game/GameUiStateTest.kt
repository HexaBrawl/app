package at.aau.serg.websocketbrokerdemo.ui.game

import at.aau.serg.websocketbrokerdemo.data.serverside.GameUnit
import at.aau.serg.websocketbrokerdemo.data.serverside.UnitType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

/**
 * Tests fuer GameUiState. Defaults + copy().
 */
class GameUiStateTest {

    @Test
    fun `default state has no selection and dashes`() {
        val state = GameUiState()
        assertNull(state.selected)
        assertEquals("-", state.lastTap)
        assertEquals("-", state.lastMove)
    }

    @Test
    fun `copy creates independent state`() {
        val unit = GameUnit(player = "X", x = 1, y = 2, type = UnitType.CAVALRY)
        val original = GameUiState(selected = unit, lastTap = "a")
        val modified = original.copy(lastTap = "b")
        assertEquals("a", original.lastTap)
        assertEquals("b", modified.lastTap)
        assertEquals(unit, modified.selected)
    }

    @Test
    fun `equals compares all fields`() {
        val a = GameUiState(lastTap = "x")
        val b = GameUiState(lastTap = "x")
        val c = GameUiState(lastTap = "y")
        assertEquals(a, b)
        assertNotEquals(a, c)
    }
}
