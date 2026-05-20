package at.aau.serg.websocketbrokerdemo.data.serverside

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MoveTest {

    @Test
    fun `Move default values are correct`() {
        val move = Move()
        assertEquals("", move.player)
        assertEquals(UnitType.INFANTRY, move.type)
        assertEquals(0, move.fromX)
        assertEquals(0, move.fromY)
        assertEquals(0, move.toX)
        assertEquals(0, move.toY)
    }

    @Test
    fun `Move custom values are correctly set`() {
        val move = Move("Player1", UnitType.ARCHER, 1, 2, 3, 4)
        assertEquals("Player1", move.player)
        assertEquals(UnitType.ARCHER, move.type)
        assertEquals(1, move.fromX)
        assertEquals(2, move.fromY)
        assertEquals(3, move.toX)
        assertEquals(4, move.toY)
    }
}
