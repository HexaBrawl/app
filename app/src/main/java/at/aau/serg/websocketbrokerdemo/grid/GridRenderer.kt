package at.aau.serg.websocketbrokerdemo.grid

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
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
    for (row in 0 until rows) {
        for (col in 0 until cols) {
            if (!ShapeUtils.isInShape(shape, col, row)) continue

            val (centerX, centerY) = getHexCenter(col, row, layout)

            val path = Path()
            for (i in 0..5) {
                val angle = Math.toRadians((60 * i).toDouble())
                val x = centerX + layout.hexSize * cos(angle).toFloat()
                val y = centerY + layout.hexSize * sin(angle).toFloat()

                if (i == 0) path.moveTo(x, y)
                else path.lineTo(x, y)
            }
            path.close()

            drawPath(
                path = path,
                color = Color.Black,
                style = Stroke(width = 3f)
            )

            val unit = units.find { it.x == col && it.y == row }
            if (unit != null) {
                val colorInt = PlayerColors.getColorForPlayer(unit.player)

                drawCircle(
                    color = Color(colorInt),
                    radius = layout.hexSize / 2.5f,
                    center = Offset(centerX, centerY)
                )
            }
        }
    }
}