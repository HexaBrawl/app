package at.aau.serg.websocketbrokerdemo.ui.mainmenu

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class HotspotCalculatorTest {

    @Test
    fun `computes center for wide screen`() {
        val (x, y) = HotspotCalculator.computeCenter(
            parentW = 2000f,
            parentH = 1000f,
            xPct = 0.5f,
            yPct = 0.5f
        )

        Assertions.assertTrue(x > 0f)
        Assertions.assertTrue(y > 0f)
    }

    @Test
    fun `computes center for tall screen`() {
        val (x, y) = HotspotCalculator.computeCenter(
            parentW = 1000f,
            parentH = 2000f,
            xPct = 0.3f,
            yPct = 0.7f
        )

        Assertions.assertTrue(x > 0f)
        Assertions.assertTrue(y > 0f)
    }

    @Test
    fun `center moves correctly with percentage`() {
        val (x1, _) = HotspotCalculator.computeCenter(1000f, 2000f, 0.1f, 0.5f)
        val (x2, _) = HotspotCalculator.computeCenter(1000f, 2000f, 0.9f, 0.5f)

        Assertions.assertTrue(x2 > x1)
    }
}