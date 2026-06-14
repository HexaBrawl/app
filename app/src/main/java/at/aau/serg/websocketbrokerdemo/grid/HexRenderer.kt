package at.aau.serg.websocketbrokerdemo.grid

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import at.aau.serg.websocketbrokerdemo.data.serverside.Building
import at.aau.serg.websocketbrokerdemo.data.serverside.BuildingType
import at.aau.serg.websocketbrokerdemo.data.serverside.Field
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
     * Reihenfolge pro Zelle: Fuellung eroberter Felder -> Hex-Rand ->
     * Gebaeude -> Einheit, damit Rand und Icons immer ueber der Markierung liegen.
     *
     *
     * @param layout  Hex-Geometrie der aktuellen Karte
     * @param units   Liste der Einheiten (GameUnit)
     * @param buildings Liste der Gebaeude (Building)
     * @param fields  Hex-Felder mit Besitzer-Info aus dem GameState;
     *                eroberte Felder (owner != null) werden halbtransparent
     *                in der Spielerfarbe gefuellt (subissue #123)
     * @param players Volle Spieler-Liste aus dem GameState fuer das
     *                Farb-Mapping
     * @param unitPainters Vorab geladene Painter fuer die Einheiten-Icons
     * @param buildingPainters Vorab geladene Painter fuer die Gebaeude-Icons
     */
    fun DrawScope.render(
        layout: MapLayout,
        units: List<GameUnit>,
        buildings: List<Building>,
        fields: List<Field>,
        players: List<Player>,
        unitPainters: Map<Pair<PlayerColor, UnitType>, Painter>,
        buildingPainters: Map<Pair<PlayerColor, BuildingType>, Painter>,
        darkenedCells: Set<Pair<Int, Int>> = emptySet(),
        highlightedCells: Set<Pair<Int, Int>> = emptySet()
    ) {
        val unitsByPosition = units.associateBy { it.x to it.y }
        val buildingsByPosition = buildings.associateBy { it.x to it.y }
        val fieldsByPosition = fields.associateBy { it.x to it.y }
        val playerMap = players.associateBy { it.name }

        for ((col, row) in HexGridLogic.allCells(layout)) {
            val (cx, cy) = HexGridLogic.cellCenter(col, row, layout)

            // Eroberte Felder zuerst fuellen, damit Rand und Icons
            // darueber liegen
            fieldsByPosition[col to row]?.owner?.let { owner ->
                val fillColor = PlayerColorMap.cellFillFor(owner, players)
                drawCellFill(cx, cy, layout.hexSize, fillColor)
            }

            // Gueltige Zielfelder (Platzieren/Bewegen) hell ueberlagern, damit
            // sie auch bei dunklen Spielerfarben (z. B. Blau) klar als "offen"
            // erkennbar sind. Der Schein liegt UNTER den Icons (die kommen
            // weiter unten), aber ueber der Owner-Fuellung.
            if ((col to row) in highlightedCells) {
                drawCellFill(cx, cy, layout.hexSize, HIGHLIGHT_FILL)
            }

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

        // Outside-Range-Overlay als letzter Schritt -- darunter sind
        // Owner-Farbe, Hex-Rand und Einheiten/Gebaeude schon gezeichnet,
        // der dunkle Overlay legt sich darueber und signalisiert
        // "ausserhalb der Reichweite".
        if (darkenedCells.isNotEmpty()) {
            for ((col, row) in darkenedCells) {
                val (cx, cy) = HexGridLogic.cellCenter(col, row, layout)
                drawCellFill(cx, cy, layout.hexSize, Color(0f, 0f, 0f, 0.45f))
            }
        }

        // Gueltige Zielfelder zum Schluss mit einem kraeftigen Gold-Rand
        // umranden (ueber Icons + Darken-Overlay), damit sie deutlich
        // "aufleuchten" und sich klar vom Rest abheben.
        if (highlightedCells.isNotEmpty()) {
            for ((col, row) in highlightedCells) {
                val (cx, cy) = HexGridLogic.cellCenter(col, row, layout)
                drawHexStroke(cx, cy, layout.hexSize, HIGHLIGHT_BORDER, HIGHLIGHT_STROKE)
            }
        }
    }

    /**
     * Fuellt ein Hex vollflaechig in der (bereits halbtransparenten)
     * Besitzer-Farbe, siehe [PlayerColorMap.cellFillFor].
     */
    private fun DrawScope.drawCellFill(cx: Float, cy: Float, size: Float, color: Color) {
        val path = Path()
        HexGridLogic.hexCorners(cx, cy, size).forEachIndexed { i, (x, y) ->
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        drawPath(path, color, style = Fill)
    }

    /** Einzelner Hex als geschlossener Pfad mit schwarzem Rand. */
    private fun DrawScope.drawHex(cx: Float, cy: Float, size: Float) =
        drawHexStroke(cx, cy, size, Color.Black, 3f)

    /** Hex-Umrandung in beliebiger Farbe/Strichstaerke. */
    private fun DrawScope.drawHexStroke(
        cx: Float,
        cy: Float,
        size: Float,
        color: Color,
        width: Float
    ) {
        val path = Path()
        HexGridLogic.hexCorners(cx, cy, size).forEachIndexed { i, (x, y) ->
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        drawPath(path, color, style = Stroke(width))
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
        val iconSize = radius * 3.75f // Erhoeht von 2.5f auf 3.75f (+50%)

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

    private companion object {
        /**
         * Dezenter heller Schein ueber gueltigen Zielfeldern -- hellt dunkle
         * Farben nur leicht auf, sodass die Spielerfarbe darunter sichtbar
         * bleibt. Der goldene Rand ([HIGHLIGHT_BORDER]) ist der eigentliche
         * Hervorhebungs-Indikator.
         */
        val HIGHLIGHT_FILL = Color(1f, 1f, 1f, 0.01f)

        /** Kraeftiger Gold-Rand, der gueltige Zielfelder umrandet. */
        val HIGHLIGHT_BORDER = Color(0xFFFFE082)

        /** Strichstaerke des Highlight-Rands (dicker als der normale Hex-Rand). */
        const val HIGHLIGHT_STROKE = 6f
    }
}
