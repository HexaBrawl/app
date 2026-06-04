package at.aau.serg.websocketbrokerdemo.grid

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class UnitDataTest {

    // ---- Grundlegende Erstellung --------------------------------------

    @Test
    fun `UnitData speichert x, y und player korrekt`() {
        val unit = UnitData(x = 2, y = 3, player = "Alice")
        assertEquals(2, unit.x)
        assertEquals(3, unit.y)
        assertEquals("Alice", unit.player)
    }

    // ---- Equality (data class) ----------------------------------------

    @Test
    fun `Zwei UnitData mit gleichen Werten sind gleich`() {
        val a = UnitData(x = 1, y = 1, player = "Bob")
        val b = UnitData(x = 1, y = 1, player = "Bob")
        assertEquals(a, b)
    }

    @Test
    fun `UnitData mit unterschiedlicher Position sind nicht gleich`() {
        val a = UnitData(x = 0, y = 0, player = "Alice")
        val b = UnitData(x = 1, y = 0, player = "Alice")
        assertNotEquals(a, b)
    }

    @Test
    fun `UnitData mit unterschiedlichem Player sind nicht gleich`() {
        val a = UnitData(x = 0, y = 0, player = "Alice")
        val b = UnitData(x = 0, y = 0, player = "Bob")
        assertNotEquals(a, b)
    }

    // ---- Copy (data class) --------------------------------------------

    @Test
    fun `copy veraendert nur den angegebenen Wert`() {
        val original = UnitData(x = 1, y = 2, player = "Alice")
        val moved = original.copy(x = 5)
        assertEquals(5, moved.x)
        assertEquals(2, moved.y)
        assertEquals("Alice", moved.player)
    }

    // ---- hashCode -----------------------------------------------------

    @Test
    fun `Gleiche UnitData haben denselben hashCode`() {
        val a = UnitData(x = 3, y = 4, player = "Charlie")
        val b = UnitData(x = 3, y = 4, player = "Charlie")
        assertEquals(a.hashCode(), b.hashCode())
    }
}
