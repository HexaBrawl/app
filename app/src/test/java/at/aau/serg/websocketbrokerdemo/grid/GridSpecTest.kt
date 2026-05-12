package at.aau.serg.websocketbrokerdemo.grid

import at.aau.serg.websocketbrokerdemo.grid.library.GridSpec
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GridSpecTest {

    @Test
    fun `should correctly store rows cols and name`() {
        val spec = GridSpec(rows = 10, cols = 12, name = "Test Grid")

        assertEquals(10, spec.rows)
        assertEquals(12, spec.cols)
        assertEquals("Test Grid", spec.name)
    }

    @Test
    fun `two GridSpecs with same values should be equal`() {
        val a = GridSpec(8, 8, "Grid")
        val b = GridSpec(8, 8, "Grid")

        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `two GridSpecs with different values should not be equal`() {
        val a = GridSpec(8, 8, "Grid")
        val b = GridSpec(10, 10, "Other")

        assertNotEquals(a, b)
    }
}
