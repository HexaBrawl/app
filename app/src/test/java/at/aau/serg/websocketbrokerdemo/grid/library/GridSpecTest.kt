package at.aau.serg.websocketbrokerdemo.grid.library

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class GridSpecTest {

    @Test
    fun `should correctly store rows cols and name`() {
        val spec = GridSpec(rows = 10, cols = 12, name = "Test Grid")

        Assertions.assertEquals(10, spec.rows)
        Assertions.assertEquals(12, spec.cols)
        Assertions.assertEquals("Test Grid", spec.name)
    }

    @Test
    fun `two GridSpecs with same values should be equal`() {
        val a = GridSpec(8, 8, "Grid")
        val b = GridSpec(8, 8, "Grid")

        Assertions.assertEquals(a, b)
        Assertions.assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `two GridSpecs with different values should not be equal`() {
        val a = GridSpec(8, 8, "Grid")
        val b = GridSpec(10, 10, "Other")

        Assertions.assertNotEquals(a, b)
    }
}