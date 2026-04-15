package at.aau.serg.websocketbrokerdemo.grid

import org.junit.Assert.*
import org.junit.Test

class HexMathTest {

    private val layout = GridLayout(
        hexSize = 50f,
        hSpacing = 75f,
        vSpacing = 86.6f,
        offsetX = 100f,
        offsetY = 100f
    )

    @Test
    fun `getHexCenter returns deterministic positions`() {
        val (x1, y1) = getHexCenter(0, 0, layout)
        val (_, y2) = getHexCenter(0, 1, layout)

        assertTrue(x1 == layout.offsetX)
        assertTrue(y2 > y1)
    }

    @Test
    fun `odd column shifts y position`() {
        val (_, yEven) = getHexCenter(0, 0, layout)
        val (_, yOdd) = getHexCenter(1, 0, layout)

        assertNotEquals(yEven, yOdd)
    }

    @Test
    fun `point inside hex detection works`() {
        val inside = isPointInHex(
            x = 100f,
            y = 100f,
            centerX = 100f,
            centerY = 100f,
            hexSize = 50f
        )

        assertTrue(inside)
    }

    @Test
    fun `point far away is not inside hex`() {
        val outside = isPointInHex(
            x = 500f,
            y = 500f,
            centerX = 100f,
            centerY = 100f,
            hexSize = 50f
        )

        assertFalse(outside)
    }
}