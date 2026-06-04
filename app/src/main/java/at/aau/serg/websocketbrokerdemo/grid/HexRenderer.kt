package at.aau.serg.websocketbrokerdemo.grid

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import at.aau.serg.websocketbrokerdemo.data.serverside.GameUnit
import at.aau.serg.websocketbrokerdemo.data.serverside.Player

/**
 * Zeichnet das Hex-Grid samt Einheiten in einen Compose-DrawScope.
 *
 * Benoetigt die aktuelle Spieler-Liste vom Server, um pro Einheit die
 * richtige Farbe ueber [PlayerColorMap.colorFor] aufzuloesen.
 */
class HexRenderer {

    /**
     * Zeichnet alle Zellen und die darauf stehenden Einheiten.
     *
     * @param layout  Hex-Geometrie der aktuellen Karte
     * @param units   Liste der Einheiten (GameUnit)
     * @param players Volle Spieler-Liste aus dem GameState fuer das
     *                Farb-Mapping
     */
    fun DrawScope.render(
        layout: MapLayout,
        units: List<GameUnit>,
        players: List<Player>
    ) {
        val unitsByPosition = units.associateBy { it.x to it.y }

        for ((col, row) in HexGridLogic.allCells(layout)) {
            val (cx, cy) = HexGridLogic.cellCenter(col, row, layout)

            drawHex(cx, cy, layout.hexSize)

            unitsByPosition[col to row]?.let { unit ->
                drawUnit(unit.player, players, cx, cy, layout.hexSize)
            }
        }
    }

    /** Einzelner Hex als geschlossener Pfad mit schwarzem Rand. */
    private fun DrawScope.drawHex(cx: Float, cy: Float, size: Float) {
        val path = Path()
        HexGridLogic.hexCorners(cx, cy, size).forEachIndexed { i, (x, y) ->
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        drawPath(path, Color.Black, style = Stroke(3f))
    }

    /** Einheit als gefuellter Kreis in Spieler-Farbe. */
    private fun DrawScope.drawUnit(
        playerName: String,
        players: List<Player>,
        cx: Float,
        cy: Float,
        size: Float
    ) {
        drawCircle(
            color = PlayerColorMap.colorFor(playerName, players),
            radius = HexGridLogic.unitRadius(size),
            center = Offset(cx, cy)
        )
    }
}
