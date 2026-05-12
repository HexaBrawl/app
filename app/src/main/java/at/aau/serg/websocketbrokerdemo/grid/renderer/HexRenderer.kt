package at.aau.serg.websocketbrokerdemo.grid.renderer

import androidx.compose.ui.graphics.drawscope.DrawScope
import at.aau.serg.websocketbrokerdemo.grid.model.GridModel

class HexRenderer(
    private val drawer: HexDrawer
) : GridRenderer {

    override fun DrawScope.render(model: GridModel) {
        val layout = model.layout
        val units = model.units.associateBy { it.x to it.y }

        for ((col, row) in model.shape.allCells(model.width, model.height)) {
            val (cx, cy) = layout.cellCenter(col, row)

            drawer.drawHex(this, cx, cy, layout.cellSize)

            units[col to row]?.let { unit ->
                drawer.drawUnit(this, unit.player, cx, cy, layout.cellSize)
            }
        }
    }
}
