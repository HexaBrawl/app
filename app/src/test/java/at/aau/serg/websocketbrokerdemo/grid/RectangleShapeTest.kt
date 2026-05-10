package at.aau.serg.websocketbrokerdemo.grid

import at.aau.serg.websocketbrokerdemo.grid.shape.RectangleShape
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class RectangleShapeTest {

    @Test
    fun `isInside should always return true`() {
        assertTrue(RectangleShape.isInside(0, 0))
        assertTrue(RectangleShape.isInside(5, 10))
        assertTrue(RectangleShape.isInside(-1, -1)) // auch negative Werte
    }

    @Test
    fun `allCells should return all cells in grid`() {
        val result = RectangleShape.allCells(width = 2, height = 2).toList()

        assertEquals(
            listOf(
                0 to 0,
                1 to 0,
                0 to 1,
                1 to 1
            ),
            result
        )
    }
}
