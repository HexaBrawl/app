package at.aau.serg.websocketbrokerdemo.grid.renderer

import kotlin.math.cos
import kotlin.math.sin

fun computeHexPoints(cx: Float, cy: Float, size: Float): List<Pair<Float, Float>> {
    return (0..5).map { i ->
        val angle = Math.toRadians((60 * i).toDouble())
        val x = cx + size * cos(angle).toFloat()
        val y = cy + size * sin(angle).toFloat()
        x to y
    }
}

fun computeUnitRadius(size: Float): Float {
    return size / 2.5f
}
