package at.aau.serg.websocketbrokerdemo.grid.layout

import kotlin.math.sqrt

class HexLayout(
    val hexSize: Float,
    val rows: Int,
    val cols: Int
) : GridLayout {

    override val cellSize: Float = hexSize

    private val hexWidth = hexSize * 2f
    private val hexHeight = sqrt(3f) * hexSize

    private val hSpacing = hexWidth * 0.75f
    private val vSpacing = hexHeight

    // ⭐ ZENTRIERUNG: Grid-Mitte liegt bei (0,0)
    private val offsetX = -((cols - 1) * hSpacing) / 2f
    private val offsetY = -((rows - 1) * vSpacing) / 2f

    override fun cellCenter(col: Int, row: Int): Pair<Float, Float> {
        val x = offsetX + col * hSpacing
        val y = offsetY + row * vSpacing + if (col % 2 == 1) vSpacing / 2f else 0f
        return x to y
    }

    override fun pixelToCell(px: Float, py: Float): Pair<Int, Int>? {
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val (cx, cy) = cellCenter(col, row)
                val dx = px - cx
                val dy = py - cy
                val dist = kotlin.math.sqrt(dx * dx + dy * dy)
                if (dist < hexSize) return col to row
            }
        }
        return null
    }
}
