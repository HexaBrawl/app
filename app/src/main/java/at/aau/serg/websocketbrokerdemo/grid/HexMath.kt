package at.aau.serg.websocketbrokerdemo.grid

import kotlin.math.sqrt

fun getHexCenter(
    col: Int,
    row: Int,
    layout: GridLayout
): Pair<Float, Float> {
    val centerX = layout.offsetX + col * layout.hSpacing
    var centerY = layout.offsetY + row * layout.vSpacing

    if (col % 2 == 1) {
        centerY += layout.vSpacing / 2
    }

    return centerX to centerY
}

fun isPointInHex(
    x: Float,
    y: Float,
    centerX: Float,
    centerY: Float,
    hexSize: Float
): Boolean {
    val dx = x - centerX
    val dy = y - centerY
    val distance = sqrt(dx * dx + dy * dy)
    return distance < hexSize
}