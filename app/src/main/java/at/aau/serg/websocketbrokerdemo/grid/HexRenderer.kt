package at.aau.serg.websocketbrokerdemo.grid

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import at.aau.serg.websocketbrokerdemo.data.serverside.Building
import at.aau.serg.websocketbrokerdemo.data.serverside.BuildingType
import at.aau.serg.websocketbrokerdemo.data.serverside.GameUnit
import at.aau.serg.websocketbrokerdemo.data.serverside.Player
import at.aau.serg.websocketbrokerdemo.data.serverside.PlayerColor
import at.aau.serg.websocketbrokerdemo.data.serverside.UnitType

/**
 * Zeichnet das Hex-Grid samt Einheiten und Gebaeuden in einen Compose-DrawScope.
 *
 * Benoetigt die aktuelle Spieler-Liste vom Server, um pro Einheit/Gebaeude die
 * richtige Farbe aufzuloesen.
 */
class HexRenderer {

    /**
     * Zeichnet alle Zellen und die darauf stehenden Einheiten/Gebaeude.
     *
     * @param layout  Hex-Geometrie der aktuellen Karte
     * @param units   Liste der Einheiten (GameUnit)
     * @param buildings Liste der Gebaeude (Building)
     * @param players Volle Spieler-Liste aus dem GameState fuer das
     *                Farb-Mapping
     * @param unitPainters Vorab geladene Painter fuer die Einheiten-Icons
     * @param buildingPainters Vorab geladene Painter fuer die Gebaeude-Icons
     */
    fun DrawScope.render(
        layout: MapLayout,
        units: List<GameUnit>,
        buildings: List<Building>,
        players: List<Player>,
        unitPainters: Map<Pair<PlayerColor, UnitType>, Painter>,
        buildingPainters: Map<Pair<PlayerColor, BuildingType>, Painter>
    ) {
        val unitsByPosition = units.associateBy { it.x to it.y }
        val buildingsByPosition = buildings.associateBy { it.x to it.y }
        val playerMap = players.associateBy { it.name }

        for ((col, row) in HexGridLogic.allCells(layout)) {
            val (cx, cy) = HexGridLogic.cellCenter(col, row, layout)

            drawHex(cx, cy, layout.hexSize)

            // Gebaeude zuerst zeichnen (Hintergrund fuer Einheiten)
            buildingsByPosition[col to row]?.let { building ->
                val player = playerMap[building.player]
                val color = player?.color ?: PlayerColor.RED
                drawBuilding(building, color, buildingPainters, cx, cy, layout.hexSize)
            }

            unitsByPosition[col to row]?.let { unit ->
                val player = playerMap[unit.player]
                val color = player?.color ?: PlayerColor.RED // Fallback
                drawUnit(unit, color, unitPainters, cx, cy, layout.hexSize)
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

    /** Einheit als Icon-Painter. */
    private fun DrawScope.drawUnit(
        unit: GameUnit,
        color: PlayerColor,
        painters: Map<Pair<PlayerColor, UnitType>, Painter>,
        cx: Float,
        cy: Float,
        size: Float
    ) {
        val painter = painters[color to unit.type] ?: return
        val radius = HexGridLogic.unitRadius(size)
        val iconSize = radius * 3.75f

        translate(cx - iconSize / 2, cy - iconSize / 2) {
            with(painter) {
                draw(size = Size(iconSize, iconSize))
            }
        }
    }

    /** Gebaeude als Icon-Painter. */
    private fun DrawScope.drawBuilding(
        building: Building,
        color: PlayerColor,
        painters: Map<Pair<PlayerColor, BuildingType>, Painter>,
        cx: Float,
        cy: Float,
        size: Float
    ) {
        val painter = painters[color to building.type] ?: return
        val iconSize = size * 1.5f // Gebaeude etwas groesser als Einheiten

        translate(cx - iconSize / 2, cy - iconSize / 2) {
            with(painter) {
                draw(size = Size(iconSize, iconSize))
            }
        }
    }
}
