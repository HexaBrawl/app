package at.aau.serg.websocketbrokerdemo.grid.shape

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class GridShapeTest {

    // Test-Shape, die alle Aufrufe mitschneidet
    private class RecordingShape(
        val inside: (Int, Int) -> Boolean
    ) : GridShape {

        val calls = mutableListOf<Pair<Int, Int>>()

        override fun isInside(col: Int, row: Int): Boolean {
            calls += col to row
            return inside(col, row)
        }
    }

    @Test
    fun `allCells fully tests loops, order, if-branch and yielding`() {
        // inside = true für (col + row) % 2 == 0 → gemischte true/false Fälle
        val shape = RecordingShape { col, row -> (col + row) % 2 == 0 }

        val result = shape.allCells(width = 3, height = 2).toList()

        // 1) Schleifen müssen ALLE Zellen in row-major order durchlaufen
        Assertions.assertEquals(
            listOf(
                0 to 0, 1 to 0, 2 to 0,
                0 to 1, 1 to 1, 2 to 1
            ),
            shape.calls,
            "isInside must be called for every cell in row-major order"
        )

        // 2) Nur Zellen yielden, bei denen inside == true (if-Bedingung)
        Assertions.assertEquals(
            listOf(
                0 to 0, // 0+0 = 0 → true
                2 to 0, // 2+0 = 2 → true
                1 to 1  // 1+1 = 2 → true
            ),
            result,
            "allCells must yield only cells where isInside returned true"
        )
    }

    @Test
    fun `allCells returns empty and does not call isInside when width or height is zero`() {
        val shape = RecordingShape { _, _ -> true }

        Assertions.assertTrue(shape.allCells(0, 5).toList().isEmpty())
        Assertions.assertTrue(shape.allCells(5, 0).toList().isEmpty())

        Assertions.assertTrue(
            shape.calls.isEmpty(),
            "isInside must not be called when loops do not run"
        )
    }

    @Test
    fun `allCells returns empty and does not call isInside when width or height is negative`() {
        val shape = RecordingShape { _, _ -> true }

        Assertions.assertTrue(shape.allCells(-1, 5).toList().isEmpty())
        Assertions.assertTrue(shape.allCells(5, -1).toList().isEmpty())

        Assertions.assertTrue(
            shape.calls.isEmpty(),
            "isInside must not be called for negative dimensions"
        )
    }
}