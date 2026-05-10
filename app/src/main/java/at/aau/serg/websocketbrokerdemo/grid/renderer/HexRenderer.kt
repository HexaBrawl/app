package at.aau.serg.websocketbrokerdemo.grid.renderer

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import at.aau.serg.websocketbrokerdemo.grid.model.GridModel
import kotlin.math.cos
import kotlin.math.sin

object HexRenderer : GridRenderer {

    override fun DrawScope.render(model: GridModel) {
        val layout = model.layout
        val units = model.units.associateBy { it.x to it.y }

        for ((col, row) in model.shape.allCells(model.width, model.height)) {
            val (cx, cy) = layout.cellCenter(col, row)

            drawHex(cx, cy, layout.cellSize)

            units[col to row]?.let { unit ->
                drawUnit(unit.player, cx, cy, layout.cellSize)
            }
        }
    }

    private fun DrawScope.drawHex(cx: Float, cy: Float, size: Float) {
        val path = Path()
        for (i in 0..5) {
            val angle = Math.toRadians((60 * i).toDouble())
            val x = cx + size * cos(angle).toFloat()
            val y = cy + size * sin(angle).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        drawPath(path, Color.Black, style = Stroke(3f))
    }

    private fun DrawScope.drawUnit(player: String, cx: Float, cy: Float, size: Float) {
        drawCircle(
            color = Color(PlayerColors.getColorForPlayer(player)),
            radius = size / 2.5f,
            center = Offset(cx, cy)
        )
    }
}
