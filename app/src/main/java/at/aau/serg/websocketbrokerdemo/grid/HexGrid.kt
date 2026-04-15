package at.aau.serg.websocketbrokerdemo.grid

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

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
        modifier = modifier.pointerInput(shape, units) {
            detectTapGestures { offset ->
                val layout = calculateGridLayout(
                    size.width.toFloat(),
                    size.height.toFloat(),
                    rows,
                    cols
                )

                for (row in 0 until rows) {
                    for (col in 0 until cols) {
                        if (!ShapeUtils.isInShape(shape, col, row)) continue

                        val (centerX, centerY) = getHexCenter(col, row, layout)

                        if (isPointInHex(
                                offset.x,
                                offset.y,
                                centerX,
                                centerY,
                                layout.hexSize
                            )
                        ) {
                            onHexClicked(col, row)
                            return@detectTapGestures
                        }
                    }
                }
            }
        }
    ) {
        val layout = calculateGridLayout(size.width, size.height, rows, cols)

        drawHexGrid(rows, cols, layout, units, shape)
    }
}