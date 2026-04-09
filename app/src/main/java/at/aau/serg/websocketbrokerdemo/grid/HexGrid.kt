package at.aau.serg.websocketbrokerdemo.grid

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun HexGrid(
    onHexClicked: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
    units: List<UnitData> = emptyList(),
    shape: GridShape = GridShape.RECTANGLE
) {
    val rows = 8
    val cols = 8

    Canvas(
        modifier = modifier
            .pointerInput(shape, units) {
                detectTapGestures { offset ->
                    val availableWidth = size.width.toFloat()
                    val availableHeight = size.height.toFloat()

                    val hexSize = min(
                        availableWidth / (cols * 1.5f),
                        availableHeight / (rows * sqrt(3f))
                    )

                    val hSpacing = hexSize * 1.5f
                    val vSpacing = hexSize * sqrt(3f)

                    val gridWidth = (cols - 1) * hSpacing + hexSize * 2
                    val gridHeight = rows * vSpacing + vSpacing / 2

                    val offsetX = (availableWidth - gridWidth) / 2 + hexSize
                    val offsetY = (availableHeight - gridHeight) / 2 + hexSize

                    for (row in 0 until rows) {
                        for (col in 0 until cols) {
                            if (!ShapeUtils.isInShape(shape, col, row)) continue

                            val centerX = offsetX + col * hSpacing
                            var centerY = offsetY + row * vSpacing

                            if (col % 2 == 1) {
                                centerY += vSpacing / 2
                            }

                            val dx = offset.x - centerX
                            val dy = offset.y - centerY
                            val distance = sqrt(dx * dx + dy * dy)

                            if (distance < hexSize) {
                                onHexClicked(col, row)
                                return@detectTapGestures
                            }
                        }
                    }
                }
            }
    ) {
        val availableWidth = size.width
        val availableHeight = size.height

        val hexSize = min(
            availableWidth / (cols * 1.5f),
            availableHeight / (rows * sqrt(3f))
        )

        val hSpacing = hexSize * 1.5f
        val vSpacing = hexSize * sqrt(3f)

        val gridWidth = (cols - 1) * hSpacing + hexSize * 2
        val gridHeight = rows * vSpacing + vSpacing / 2

        val offsetX = (availableWidth - gridWidth) / 2 + hexSize
        val offsetY = (availableHeight - gridHeight) / 2 + hexSize

        for (row in 0 until rows) {
            for (col in 0 until cols) {
                if (!ShapeUtils.isInShape(shape, col, row)) continue

                val centerX = offsetX + col * hSpacing
                var centerY = offsetY + row * vSpacing

                // staggered columns
                if (col % 2 == 1) {
                    centerY += vSpacing / 2
                }

                // Draw hex outline
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

                // Draw units
                val unit = units.find { it.x == col && it.y == row }
                if (unit != null) {
                    val colorInt = PlayerColors.getColorForPlayer(unit.player)
                    drawCircle(
                        color = Color(colorInt),
                        radius = hexSize / 2.5f,
                        center = Offset(centerX, centerY),
                        style = Fill
                    )
                }
            }
        }
    }
}
