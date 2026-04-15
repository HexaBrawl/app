package at.aau.serg.websocketbrokerdemo.grid

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.sin

fun DrawScope.drawHexGrid(
    rows: Int,
    cols: Int,
    layout: GridLayout,
    units: List<UnitData>,
    shape: GridShape
) {
    val unitMap = units.associateBy { it.x to it.y }

    for (row in 0 until rows) {
        for (col in 0 until cols) {

            if (!ShapeUtils.isInShape(shape, col, row)) continue

            val (centerX, centerY) = getHexCenter(col, row, layout)
            val unit = unitMap[col to row]

            drawHex(centerX, centerY, layout.hexSize)

            if (unit != null) {
                drawUnit(unit, centerX, centerY, layout.hexSize)
            }
        }
    }
}

private fun DrawScope.drawHex(
    centerX: Float,
    centerY: Float,
    hexSize: Float
) {
    val path = Path()

    for (i in 0..5) {
        val angle = Math.toRadians((60 * i).toDouble())
        val x = centerX + hexSize * cos(angle).toFloat()
        val y = centerY + hexSize * sin(angle).toFloat()

        if (i == 0) path.moveTo(x, y)
        else path.lineTo(x, y)
    }

    path.close()

    drawPath(
        path = path,
        color = Color.Black,
        style = Stroke(width = 3f)
    )
}

private fun DrawScope.drawUnit(
    unit: UnitData,
    centerX: Float,
    centerY: Float,
    hexSize: Float
) {
    val colorInt = PlayerColors.getColorForPlayer(unit.player)

    drawCircle(
        color = Color(colorInt),
        radius = hexSize / 2.5f,
        center = Offset(centerX, centerY)
    )
}