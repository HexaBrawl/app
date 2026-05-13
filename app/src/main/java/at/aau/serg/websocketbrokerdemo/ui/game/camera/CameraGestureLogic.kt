package at.aau.serg.websocketbrokerdemo.ui.game.camera

object CameraGestureLogic {

    fun computeNewScale(oldScale: Float, zoom: Float, min: Float, max: Float): Float =
        (oldScale * zoom).coerceIn(min, max)

    fun computeNewOffset(
        oldOffset: Float,
        pan: Float,
        pivot: Float,
        effectiveZoom: Float
    ): Float =
        (oldOffset + pan) - (pivot - oldOffset) * (effectiveZoom - 1f)
}
