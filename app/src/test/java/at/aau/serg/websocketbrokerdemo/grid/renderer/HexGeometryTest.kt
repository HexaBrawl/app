package at.aau.serg.websocketbrokerdemo.grid.renderer

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.math.abs

class HexGeometryTest {

    @Test
    fun `computeHexPoints returns six points`() {
        val points = computeHexPoints(cx = 0f, cy = 0f, size = 10f)

        Assertions.assertEquals(6, points.size)
    }

    @Test
    fun `computeHexPoints first point is on the right`() {
        val points = computeHexPoints(cx = 0f, cy = 0f, size = 10f)

        val (x0, y0) = points[0]
        // i = 0 → angle = 0 → (size, 0)
        Assertions.assertEquals(10f, x0, 0.0001f)
        Assertions.assertEquals(0f, y0, 0.0001f)
    }

    @Test
    fun `computeHexPoints are centered around cx cy`() {
        val cx = 100f
        val cy = 200f
        val size = 10f

        val points = computeHexPoints(cx, cy, size)

        // Nur sanity check: alle Punkte sind in der Nähe des Zentrums
        points.forEach { (x, y) ->
            // Abstand in x und y darf nicht größer als size sein
            assert((x - cx).toDouble().let { abs(it) } <= size + 0.0001)
            assert((y - cy).toDouble().let { abs(it) } <= size + 0.0001)
        }
    }

    @Test
    fun `computeUnitRadius scales with size`() {
        val size = 25f
        val radius = computeUnitRadius(size)

        Assertions.assertEquals(size / 2.5f, radius, 0.0001f)
    }
}