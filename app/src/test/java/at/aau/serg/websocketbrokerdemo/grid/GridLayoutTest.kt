package at.aau.serg.websocketbrokerdemo.grid

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GridLayoutTest {

    @Test
    fun `grid layout produces valid hex sizing`() {
        val layout = calculateGridLayout(
            width = 1000f,
            height = 1000f,
            rows = 8,
            cols = 8
        )

        assertTrue(layout.hexSize > 0)
        assertTrue(layout.hSpacing > layout.hexSize)
        assertTrue(layout.vSpacing > 0)
    }

    @Test
    fun `offsets center grid roughly in screen`() {
        val layout = calculateGridLayout(1000f, 1000f, 8, 8)

        assertTrue(layout.offsetX > 0)
        assertTrue(layout.offsetY > 0)
    }
}