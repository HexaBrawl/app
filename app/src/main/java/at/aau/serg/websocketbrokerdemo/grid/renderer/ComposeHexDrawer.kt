package at.aau.serg.websocketbrokerdemo.grid.renderer

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

open class ComposeHexDrawer : HexDrawer {

    override fun drawHex(scope: DrawScope, cx: Float, cy: Float, size: Float) {
        val path = Path()
        val points = computeHexPoints(cx, cy, size)

        points.forEachIndexed { index, (x, y) ->
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        path.close()
        scope.drawPath(path, Color.Black, style = Stroke(3f))
    }

    override fun drawUnit(scope: DrawScope, player: String, cx: Float, cy: Float, size: Float) {
        val radius = computeUnitRadius(size)

        scope.drawCircle(
            color = Color(PlayerColors.getColorForPlayer(player)),
            radius = radius,
            center = Offset(cx, cy)
        )
    }
}