package at.aau.serg.websocketbrokerdemo.ui.game

import androidx.compose.ui.unit.IntSize
import at.aau.serg.websocketbrokerdemo.ui.game.camera.CameraState
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class CameraStateTest {

    @Test
    fun `clampOffset returns original values when viewport is zero`() {
        val camera = CameraState()
        camera.viewportSize.value = IntSize.Companion.Zero

        val result = camera.clampOffset(50f, -30f)

        Assertions.assertEquals(50f to -30f, result)
    }

    @Test
    fun `clampOffset clamps values based on viewport and scale`() {
        val camera = CameraState(mapSizeFactor = 1f)
        camera.viewportSize.value = IntSize(1000, 800)
        camera.scale.floatValue = 2f

        // Berechnung:
        // excessX = (1000 * 2 - 1000) / 2 = 500
        // excessY = (800 * 2 - 800) / 2 = 400

        val result = camera.clampOffset(600f, -500f)

        Assertions.assertEquals(500f, result.first)   // maxX
        Assertions.assertEquals(-400f, result.second) // -maxY
    }

    @Test
    fun `clampOffset allows movement inside bounds`() {
        val camera = CameraState()
        camera.viewportSize.value = IntSize(1000, 800)
        camera.scale.floatValue = 1.5f

        val result = camera.clampOffset(100f, -50f)

        Assertions.assertEquals(100f, result.first)
        Assertions.assertEquals(-50f, result.second)
    }

    @Test
    fun `clampOffset respects mapSizeFactor`() {
        val camera = CameraState(mapSizeFactor = 2f)
        camera.viewportSize.value = IntSize(1000, 800)
        camera.scale.floatValue = 1f

        // excessX = (1000 * 2 - 1000) / 2 = 500
        // excessY = (800 * 2 - 800) / 2 = 400

        val result = camera.clampOffset(-600f, 500f)

        Assertions.assertEquals(-500f, result.first)
        Assertions.assertEquals(400f, result.second)
    }
}