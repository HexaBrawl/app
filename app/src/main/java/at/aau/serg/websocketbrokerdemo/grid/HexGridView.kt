package at.aau.serg.websocketbrokerdemo.grid

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.min
import kotlin.math.sqrt
import android.graphics.Color

class HexGridView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    interface OnHexClickListener {
        fun onHexClicked(x: Int, y: Int)
    }
    var units: List<UnitData> = emptyList()
    var listener: OnHexClickListener? = null
    var shape: GridShape = GridShape.RECTANGLE

    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }
    private val rows = 8
    private val cols = 8

    //  Shared variables (used for drawing + touch)
    private var hexSize = 0f
    private var offsetX = 0f
    private var offsetY = 0f
    private var hSpacing = 0f
    private var vSpacing = 0f

    init {
        isClickable = true
        isFocusable = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //  1. Calculate layout
        val availableWidth = width.toFloat()
        val availableHeight = height.toFloat()

        hexSize = min(
            availableWidth / (cols * 1.5f),
            availableHeight / (rows * sqrt(3f))
        )

        hSpacing = hexSize * 1.5f
        vSpacing = hexSize * sqrt(3f)

        val gridWidth = (cols - 1) * hSpacing + hexSize * 2
        val gridHeight = rows * vSpacing + vSpacing / 2

        offsetX = (availableWidth - gridWidth) / 2 + hexSize
        offsetY = (availableHeight - gridHeight) / 2 + hexSize

        // Debug
        //Log.e("HexGrid", "Units: $units")

        //  2. Draw grid
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                if(!ShapeUtils.isInShape(shape, col, row)) continue

                val centerX = offsetX + col * hSpacing
                var centerY = offsetY + row * vSpacing

                // staggered columns
                if (col % 2 == 1) {
                    centerY += vSpacing / 2
                }

                //  Draw hex outline
                paint.style = Paint.Style.STROKE
                paint.color = Color.BLACK
                drawHex(canvas, centerX, centerY, hexSize)

                // Draw units (optimized)
                val unit = units.find { it.x == col && it.y == row }

                if (unit != null) {
                    paint.style = Paint.Style.FILL
                    paint.color = PlayerColors.getColorForPlayer(unit.player)
                    canvas.drawCircle(
                        centerX,
                        centerY,
                        hexSize / 2.5f,
                        paint
                    )
                }
            }
        }
    }

    private fun drawHex(canvas: Canvas, cx: Float, cy: Float, size: Float) {
        val path = Path()

        for (i in 0..5) {
            val angle = Math.toRadians((60 * i).toDouble())

            val x = cx + size * Math.cos(angle).toFloat()
            val y = cy + size * Math.sin(angle).toFloat()

            if (i == 0) path.moveTo(x, y)
            else path.lineTo(x, y)
        }

        path.close()
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {

            val touchX = event.x
            val touchY = event.y

            handleTouch(touchX, touchY)

            return true
        }
        return super.onTouchEvent(event)
    }

    private fun handleTouch(touchX: Float, touchY: Float) {

        for (row in 0 until rows) {
            for (col in 0 until cols) {
                if(!ShapeUtils.isInShape(shape, col, row)) continue

                val centerX = offsetX + col * hSpacing
                var centerY = offsetY + row * vSpacing

                if (col % 2 == 1) {
                    centerY += vSpacing / 2
                }

                val dx = touchX - centerX
                val dy = touchY - centerY

                val distance = sqrt(dx * dx + dy * dy)

                if (distance < hexSize) {
                    println("CLICK DETECTED: $col,$row")
                    listener?.onHexClicked(col, row)
                    return
                }
            }
        }
    }
}