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
    fun `default state has no selection and no placement`() {
        val state = GameUiState()
        assertNull(state.selected)
        assertNull(state.placementMode)
    }

    @Test
    fun `copy creates independent state`() {
        val unit = GameUnit(player = "X", x = 1, y = 2, type = UnitType.CAVALRY)
        val original = GameUiState()
        val modified = original.copy(selected = unit, placementMode = UnitType.INFANTRY)
        assertNull(original.selected)
        assertEquals(unit, modified.selected)
        assertEquals(UnitType.INFANTRY, modified.placementMode)
    }

    @Test
    fun `equals compares all fields`() {
        val unit = GameUnit(player = "X", x = 1, y = 2, type = UnitType.CAVALRY)
        val a = GameUiState(selected = unit, placementMode = UnitType.ARCHER)
        val b = GameUiState(selected = unit, placementMode = UnitType.ARCHER)
        val c = GameUiState(selected = unit, placementMode = UnitType.INFANTRY)
        assertEquals(a, b)
        assertNotEquals(a, c)
    }
}