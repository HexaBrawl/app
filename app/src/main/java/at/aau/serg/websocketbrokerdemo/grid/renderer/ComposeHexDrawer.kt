package at.aau.serg.websocketbrokerdemo.grid.renderer

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.sin

class ComposeHexDrawer : HexDrawer {

    override fun drawHex(scope: DrawScope, cx: Float, cy: Float, size: Float) {
        val path = Path()
        for (i in 0..5) {
            val angle = Math.toRadians((60 * i).toDouble())
            val x = cx + size * cos(angle).toFloat()
            val y = cy + size * sin(angle).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        scope.drawPath(path, Color.Black, style = Stroke(3f))
    }

    override fun drawUnit(scope: DrawScope, player: String, cx: Float, cy: Float, size: Float) {
        scope.drawCircle(
            color = Color(PlayerColors.getColorForPlayer(player)),
            radius = size / 2.5f,
            center = Offset(cx, cy)
        )
    }
}
