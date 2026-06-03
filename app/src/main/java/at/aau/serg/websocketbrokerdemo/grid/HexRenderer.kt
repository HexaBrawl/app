package at.aau.serg.websocketbrokerdemo.grid

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * Zeichnet das Hex-Grid samt Einheiten in einen Compose-DrawScope.
 *
 * Frueher war das auf vier Klassen verteilt: GridRenderer (Interface),
 * HexRenderer (Iteration), HexDrawer (Interface), ComposeHexDrawer
 * (eigentliches Zeichnen). Da alle Interfaces nur eine Implementierung
 * hatten, ist die Indirection geloescht und alles steht hier
 * zusammen. Das Strategy-Pattern war Over-Engineering -- falls spaeter
 * andere Render-Stile noetig werden, ist es einfacher hier ein
 * Interface einzufuehren als jetzt eines auf Verdacht zu pflegen.
 */
class HexRenderer {

    /**
     * Zeichnet alle Zellen und die darauf stehenden Einheiten.
     */
    fun DrawScope.render(layout: MapLayout, units: List<UnitData>) {
        val unitsByPosition = units.associateBy { it.x to it.y }

        for ((col, row) in HexGridLogic.allCells(layout)) {
            val (cx, cy) = HexGridLogic.cellCenter(col, row, layout)

            drawHex(cx, cy, layout.hexSize)

            unitsByPosition[col to row]?.let { unit ->
                drawUnit(unit.player, cx, cy, layout.hexSize)
            }
        }
    }

    /**
     * Zeichnet einen einzelnen Hex als geschlossenen Pfad mit
     * schwarzem Rand.
     */
    private fun DrawScope.drawHex(cx: Float, cy: Float, size: Float) {
        val path = Path()
        HexGridLogic.hexCorners(cx, cy, size).forEachIndexed { i, (x, y) ->
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        drawPath(path, Color.Black, style = Stroke(3f))
    }

    /**
     * Zeichnet eine Einheit als gefuellten Kreis in der Farbe ihres
     * Besitzers.
     */
    private fun DrawScope.drawUnit(player: String, cx: Float, cy: Float, size: Float) {
        drawCircle(
            color = Color(PlayerColorMap.colorFor(player)),
            radius = HexGridLogic.unitRadius(size),
            center = Offset(cx, cy)
        )
    }
}