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
    fun `default state has no selection`() {
        val state = GameUiState()
        assertNull(state.selected)
    }

    @Test
    fun `copy creates independent state`() {
        val unit = GameUnit(player = "X", x = 1, y = 2, type = UnitType.CAVALRY)
        val original = GameUiState()
        val modified = original.copy(selected = unit)
        assertNull(original.selected)
        assertEquals(unit, modified.selected)
    }

    @Test
    fun `equals compares selected field`() {
        val unit = GameUnit(player = "X", x = 1, y = 2, type = UnitType.CAVALRY)
        val a = GameUiState(selected = unit)
        val b = GameUiState(selected = unit)
        val c = GameUiState(selected = null)
        assertEquals(a, b)
        assertNotEquals(a, c)
    }
}
