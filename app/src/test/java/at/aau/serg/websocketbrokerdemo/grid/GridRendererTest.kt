package at.aau.serg.websocketbrokerdemo.grid

import org.junit.Assert.assertEquals
import org.junit.Test

class GridRendererTest {

    @Test
    fun `hex center calculation is reusable`() {
        val layout = GridLayout(
            hexSize = 50f,
            hSpacing = 75f,
            vSpacing = 86.6f,
            offsetX = 100f,
            offsetY = 100f
        )

        val (x, y) = getHexCenter(0, 0, layout)

        assertEquals(100f, x, 0.01f)
        assertEquals(100f, y, 0.01f)
    }
}