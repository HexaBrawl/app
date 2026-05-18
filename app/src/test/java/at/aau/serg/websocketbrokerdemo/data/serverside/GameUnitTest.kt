package at.aau.serg.websocketbrokerdemo.data.serverside

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GameUnitTest {

    @Test
    fun `GameUnit correctly stores values`() {
        val unit = GameUnit("Player1", 5, 10, UnitType.CAVALRY)
        assertEquals("Player1", unit.player)
        assertEquals(5, unit.x)
        assertEquals(10, unit.y)
        assertEquals(UnitType.CAVALRY, unit.type)
    }

    @Test
    fun `GameUnit default values are correct`() {
        val unit = GameUnit(player = "Player2", type = UnitType.SKELETON)
        assertEquals("Player2", unit.player)
        assertEquals(0, unit.x)
        assertEquals(0, unit.y)
        assertEquals(UnitType.SKELETON, unit.type)
    }
}
