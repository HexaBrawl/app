package at.aau.serg.websocketbrokerdemo.grid.layout

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class HexLayoutTest {

    private val hexSize = 60f
    private val rows = 5
    private val cols = 5

    private val layout = HexLayout(
        hexSize = hexSize,
        rows = rows,
        cols = cols
    )

    @Test
    fun `cellSize should match hexSize`() {
        Assertions.assertEquals(hexSize, layout.cellSize)
    }

    @Test
    fun `cellCenter should compute correct center for even column`() {
        val (x, y) = layout.cellCenter(0, 0)

        // Erste Zelle liegt exakt bei offsetX, offsetY
        Assertions.assertTrue(x < 0f)
        Assertions.assertTrue(y < 0f)
    }

    @Test
    fun `pixelToCell should detect center of a hex cell`() {
        val (cx, cy) = layout.cellCenter(2, 2)

        val result = layout.pixelToCell(cx, cy)

        Assertions.assertEquals(2 to 2, result)
    }

    @Test
    fun `pixelToCell should return null for far outside click`() {
        val result = layout.pixelToCell(9999f, 9999f)
        Assertions.assertNull(result)
    }

    @Test
    fun `pixelToCell should return null for negative coordinates`() {
        val result = layout.pixelToCell(-999f, -999f)
        Assertions.assertNull(result)
    }

    @Test
    fun `pixelToCell should detect a hex near its border`() {
        val (cx, cy) = layout.cellCenter(1, 1)

        // Klick knapp innerhalb des Radius
        val result = layout.pixelToCell(cx + hexSize * 0.9f, cy)

        Assertions.assertEquals(1 to 1, result)
    }
}