package at.aau.serg.websocketbrokerdemo.grid

import kotlin.math.min
import kotlin.math.sqrt

data class GridLayout(
    val hexSize: Float,
    val hSpacing: Float,
    val vSpacing: Float,
    val offsetX: Float,
    val offsetY: Float
)

fun calculateGridLayout(
    width: Float,
    height: Float,
    rows: Int,
    cols: Int
): GridLayout {
    val hexSize = min(
        width / (cols * 1.5f),
        height / (rows * sqrt(3f))
    )
    val hSpacing = hexSize * 1.5f
    val vSpacing = hexSize * sqrt(3f)
    val gridWidth = (cols - 1) * hSpacing + hexSize * 2
    val gridHeight = rows * vSpacing + vSpacing / 2
    val offsetX = (width - gridWidth) / 2 + hexSize
    val offsetY = (height - gridHeight) / 2 + hexSize

    return GridLayout(hexSize, hSpacing, vSpacing, offsetX, offsetY)
}