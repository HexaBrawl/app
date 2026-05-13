package at.aau.serg.websocketbrokerdemo.ui

import at.aau.serg.websocketbrokerdemo.ui.game.camera.CameraGestureLogic
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
class CameraGestureLogicTest {

    @Test
    fun `computeNewScale multiplies and clamps correctly`() {
        // normal scaling
        assertEquals(
            2f,
            CameraGestureLogic.computeNewScale(
                oldScale = 1f,
                zoom = 2f,
                min = 1f,
                max = 4f
            )
        )

        // clamp to max
        assertEquals(
            4f,
            CameraGestureLogic.computeNewScale(
                oldScale = 2f,
                zoom = 3f,
                min = 1f,
                max = 4f
            )
        )

        // clamp to min
        assertEquals(
            1f,
            CameraGestureLogic.computeNewScale(
                oldScale = 2f,
                zoom = 0.1f,
                min = 1f,
                max = 4f
            )
        )
    }

    @Test
    fun `computeNewOffset calculates correct offset`() {
        val result = CameraGestureLogic.computeNewOffset(
            oldOffset = 10f,
            pan = 5f,
            pivot = 20f,
            effectiveZoom = 1.5f
        )

        // Erwarteter Wert: (10 + 5) - (20 - 10) * (1.5 - 1)
        // = 15 - 10 * 0.5 = 15 - 5 = 10
        assertEquals(10f, result, 0.0001f)
    }

    @Test
    fun `computeNewOffset handles negative pan`() {
        val result = CameraGestureLogic.computeNewOffset(
            oldOffset = 0f,
            pan = -3f,
            pivot = 10f,
            effectiveZoom = 1.2f
        )

        // = (0 - 3) - (10 - 0) * 0.2
        // = -3 - 2 = -5
        assertEquals(-5f, result, 0.0001f)
    }

    @Test
    fun `computeNewOffset handles effectiveZoom 1 (no zoom)`() {
        val result = CameraGestureLogic.computeNewOffset(
            oldOffset = 5f,
            pan = 2f,
            pivot = 10f,
            effectiveZoom = 1f
        )

        // Kein Zoom → nur Pan
        assertEquals(7f, result, 0.0001f)
    }
}
