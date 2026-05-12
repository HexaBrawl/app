package at.aau.serg.websocketbrokerdemo.ui.game.camera

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.cameraControls(camera: CameraState): Modifier =
    this
        .pointerInput(Unit) {
            detectTransformGestures { centroid, pan, zoom, _ ->

                val oldScale = camera.scale.floatValue
                val newScale = (oldScale * zoom)
                    .coerceIn(camera.minScale, camera.maxScale)

                val effectiveZoom = newScale / oldScale

                val vp = camera.viewportSize.value
                val pivotX = centroid.x - vp.width / 2f
                val pivotY = centroid.y - vp.height / 2f

                val newOffsetX = (camera.offsetX.floatValue + pan.x) -
                        (pivotX - camera.offsetX.floatValue) * (effectiveZoom - 1f)

                val newOffsetY = (camera.offsetY.floatValue + pan.y) -
                        (pivotY - camera.offsetY.floatValue) * (effectiveZoom - 1f)

                camera.scale.floatValue = newScale

                val (cx, cy) = camera.clampOffset(newOffsetX, newOffsetY)
                camera.offsetX.floatValue = cx
                camera.offsetY.floatValue = cy
            }
        }
        .graphicsLayer {
            scaleX = camera.scale.floatValue
            scaleY = camera.scale.floatValue
            translationX = camera.offsetX.floatValue
            translationY = camera.offsetY.floatValue
        }
