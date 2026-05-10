package at.aau.serg.websocketbrokerdemo.grid

import at.aau.serg.websocketbrokerdemo.grid.model.UnitData
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class UnitDataTest {

    @Test
    fun `should correctly store x y and player`() {
        val unit = UnitData(x = 3, y = 5, player = "Player 1")

        assertEquals(3, unit.x)
        assertEquals(5, unit.y)
        assertEquals("Player 1", unit.player)
    }

    @Test
    fun `two UnitData with same values should be equal`() {
        val a = UnitData(1, 2, "P1")
        val b = UnitData(1, 2, "P1")

        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `two UnitData with different values should not be equal`() {
        val a = UnitData(1, 2, "P1")
        val b = UnitData(2, 1, "P2")

        assertNotEquals(a, b)
    }
}
