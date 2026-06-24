package at.aau.serg.websocketbrokerdemo.ui.game.camera

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Modifier, der Pinch-Zoom und Pan auf die Spielkarte anwendet.
 *
 * Faengt Transform-Gesten ab und reicht sie an die seiteneffekt-freie
 * [CameraGestureLogic] weiter: erst der neue Zoom, daraus der effektive
 * Zoom-Faktor des Frames, dann die pivot-basierten Offsets (skaliert um den
 * Gesten-Mittelpunkt statt um die Bildschirmmitte). Die Ergebnisse werden
 * in den [camera]-State geschrieben und ueber [CameraState.clampOffset] auf
 * den gueltigen Bereich begrenzt. Der `graphicsLayer`-Block uebertraegt
 * Scale und Translation anschliessend auf die gerenderte Karte.
 *
 * @param camera Beobachtbarer Kamera-State (Zoom, Offset, Viewport).
 */

fun Modifier.cameraControls(camera: CameraState): Modifier =
    this
        .pointerInput(Unit) {
            detectTransformGestures { centroid, pan, zoom, _ ->

                val oldScale = camera.scale.floatValue

                // 1) Neue Scale über ausgelagerte Logik
                val newScale = CameraGestureLogic.computeNewScale(
                    oldScale = oldScale,
                    zoom = zoom,
                    min = camera.minScale,
                    max = camera.maxScale
                )

                val effectiveZoom = newScale / oldScale

                val vp = camera.viewportSize.value
                val pivotX = centroid.x - vp.width / 2f
                val pivotY = centroid.y - vp.height / 2f

                // 2) Neue Offsets über ausgelagerte Logik
                val newOffsetX = CameraGestureLogic.computeNewOffset(
                    oldOffset = camera.offsetX.floatValue,
                    pan = pan.x,
                    pivot = pivotX,
                    effectiveZoom = effectiveZoom
                )

                val newOffsetY = CameraGestureLogic.computeNewOffset(
                    oldOffset = camera.offsetY.floatValue,
                    pan = pan.y,
                    pivot = pivotY,
                    effectiveZoom = effectiveZoom
                )

                // 3) Werte setzen
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

