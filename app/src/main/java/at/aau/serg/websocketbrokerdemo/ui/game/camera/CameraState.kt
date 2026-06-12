package at.aau.serg.websocketbrokerdemo.ui.game.camera

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.IntSize
import kotlin.math.max

/**
 * Beobachtbarer Kamera-Zustand der Spielkarte.
 *
 * Haelt Zoom (`scale`), Pan-Offset (`offsetX`/`offsetY`) und die
 * aktuelle Viewport-Groesse als Compose-States, damit Gesten und
 * Renderer ohne Recomposition-Kreislaeufe schreiben/lesen koennen.
 *
 *  - [mapSizeFactor] Verhaeltnis Map-Groesse zu Viewport-Groesse; wird
 *                    fuer die Pan-Clamping-Mathematik gebraucht. 1.0 =
 *                    Karte fuellt den Viewport bei Scale 1.
 *  - [minScale]      Untere Zoom-Grenze (Karte darf nicht weiter
 *                    "rauszoomen" als auf Pass-Groesse).
 *  - [maxScale]      Obere Zoom-Grenze (verhindert extremes Reinzoomen).
 */
class CameraState(
    val mapSizeFactor: Float = 1.0f,
    val minScale: Float = 1.0f,
    val maxScale: Float = 4.0f
) {

    /** Aktueller Zoom-Faktor. Wird vom Pinch-Gesten-Handler beschrieben. */
    var scale = mutableFloatStateOf(1f)

    /** Horizontaler Pan-Offset in Pixel. */
    var offsetX = mutableFloatStateOf(0f)

    /** Vertikaler Pan-Offset in Pixel. */
    var offsetY = mutableFloatStateOf(0f)

    /** Aktuelle Groesse der Render-Flaeche; vom Composable per `onSizeChanged` gesetzt. */
    var viewportSize = mutableStateOf(IntSize.Zero)

    /**
     * Beschraenkt einen Pan-Offset so, dass die Karte den Viewport-Rand
     * nicht ueberlaeuft.
     *
     * Berechnet aus dem aktuellen Zoom und [mapSizeFactor], wie viele
     * Pixel die skalierte Karte ueber den Viewport hinausragen, und
     * begrenzt den uebergebenen Offset auf dieses Halbintervall. Bei
     * unbekannter Viewport-Groesse passiert der Wert unveraendert
     * durch — clampen ohne Bezugsgroesse waere falsch.
     */
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
