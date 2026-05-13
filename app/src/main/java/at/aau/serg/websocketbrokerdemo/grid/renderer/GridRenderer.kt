package at.aau.serg.websocketbrokerdemo.grid.renderer

import androidx.compose.ui.graphics.drawscope.DrawScope
import at.aau.serg.websocketbrokerdemo.grid.model.GridModel

fun interface GridRenderer {
    fun DrawScope.render(model: GridModel)
}
