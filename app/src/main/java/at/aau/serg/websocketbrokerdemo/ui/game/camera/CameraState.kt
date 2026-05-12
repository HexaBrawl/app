package at.aau.serg.websocketbrokerdemo.ui.game.camera

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.IntSize
import kotlin.math.max

class CameraState(
    val mapSizeFactor: Float = 1.0f,
    val minScale: Float = 1.0f,
    val maxScale: Float = 4.0f
) {

    var scale = mutableFloatStateOf(1f)
    var offsetX = mutableFloatStateOf(0f)
    var offsetY = mutableFloatStateOf(0f)
    var viewportSize = mutableStateOf(IntSize.Zero)

    fun clampOffset(x: Float, y: Float): Pair<Float, Float> {
        val vp = viewportSize.value
        if (vp == IntSize.Zero) return x to y

        val excessX = (vp.width * mapSizeFactor * scale.floatValue - vp.width) / 2f
        val excessY = (vp.height * mapSizeFactor * scale.floatValue - vp.height) / 2f

        val maxX = max(0f, excessX)
        val maxY = max(0f, excessY)

        return x.coerceIn(-maxX, maxX) to y.coerceIn(-maxY, maxY)
    }
}
