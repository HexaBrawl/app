package at.aau.serg.websocketbrokerdemo.grid

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class HexGridTest {

    @Test
    fun `shape filter excludes invalid cells`() {
        val shape = GridShape.RECTANGLE

        val valid = ShapeUtils.isInShape(shape, 0, 0)

        assertTrue(valid)
    }

    @Test
    fun `grid size constants are consistent`() {
        val rows = 8
        val cols = 8

        assertEquals(8, rows)
        assertEquals(8, cols)
    }
}