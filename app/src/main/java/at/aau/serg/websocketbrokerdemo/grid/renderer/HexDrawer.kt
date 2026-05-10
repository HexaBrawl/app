package at.aau.serg.websocketbrokerdemo.grid.renderer

import androidx.compose.ui.graphics.drawscope.DrawScope

interface HexDrawer {
    fun drawHex(scope: DrawScope, cx: Float, cy: Float, size: Float)
    fun drawUnit(scope: DrawScope, player: String, cx: Float, cy: Float, size: Float)
}
