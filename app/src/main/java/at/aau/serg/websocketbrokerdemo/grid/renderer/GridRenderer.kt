package at.aau.serg.websocketbrokerdemo.grid.renderer

import androidx.compose.ui.graphics.drawscope.DrawScope
import at.aau.serg.websocketbrokerdemo.grid.model.GridModel

interface GridRenderer {
    fun DrawScope.render(model: GridModel)
}
