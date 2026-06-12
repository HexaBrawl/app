package at.aau.serg.websocketbrokerdemo.ui.game.camera

/**
 * Pure Mathematik fuer Pinch-Zoom- und Pan-Gesten.
 *
 * Beide Funktionen sind seiteneffekt-frei und damit ohne Compose-
 * Runtime testbar. Der Pinch-Gesten-Handler im [CameraModifier] ruft
 * sie pro Frame und schreibt die Ergebnisse in den [CameraState].
 */
object CameraGestureLogic {

    /**
     * Wendet einen Zoom-Faktor auf die aktuelle Skala an und clamped
     * auf das erlaubte Intervall.
     *
     * @param oldScale aktueller Zoom
     * @param zoom     Multiplikator aus der Geste (>1 = reinzoomen)
     * @param min      untere Zoom-Grenze (siehe [CameraState.minScale])
     * @param max      obere Zoom-Grenze (siehe [CameraState.maxScale])
     */
    fun computeNewScale(oldScale: Float, zoom: Float, min: Float, max: Float): Float =
        (oldScale * zoom).coerceIn(min, max)

    /**
     * Berechnet den neuen Pan-Offset entlang einer Achse, sodass die
     * Karte um den Geste-Mittelpunkt (`pivot`) skaliert wird statt um
     * den Bildschirm-Mittelpunkt.
     *
     * @param oldOffset     aktueller Pan-Offset auf dieser Achse
     * @param pan           Translations-Anteil der Geste (Finger-Drift)
     * @param pivot         Mittelpunkt der Geste in Viewport-Koordinaten
     * @param effectiveZoom Verhaeltnis neuer zu altem Zoom in diesem Frame
     */
    fun computeNewOffset(
        oldOffset: Float,
        pan: Float,
        pivot: Float,
        effectiveZoom: Float
    ): Float =
        (oldOffset + pan) - (pivot - oldOffset) * (effectiveZoom - 1f)
}
