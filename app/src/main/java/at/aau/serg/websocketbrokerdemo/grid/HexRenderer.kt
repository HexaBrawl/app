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
 */
class HexRenderer {

    /**
     * @param fields  Hex-Felder mit Besitzer-Info aus dem GameState;
     *                eroberte Felder werden halbtransparent in der
     *                Spielerfarbe gefuellt (subissue #123); Felder gelten
     *                als "tot" (entsaettigt, subissue #172) wenn
     *                Field.isSkeleton gesetzt ist ODER eine
     *                UnitType.SKELETON-Einheit des Owners darauf steht
     *                (Insolvenz aus applyEconomy)
     */
    fun DrawScope.render(
        layout: MapLayout,
        units: List<GameUnit>,
        buildings: List<Building>,
        fields: List<Field>,
        players: List<Player>,
        unitPainters: Map<Pair<PlayerColor, UnitType>, Painter>,
        buildingPainters: Map<Pair<PlayerColor, BuildingType>, Painter>,
        darkenedCells: Set<Pair<Int, Int>> = emptySet()
    ) {
        val unitsByPosition = units.associateBy { it.x to it.y }
        val buildingsByPosition = buildings.associateBy { it.x to it.y }
        val fieldsByPosition = fields.associateBy { it.x to it.y }
        val playerMap = players.associateBy { it.name }

        // Einmal pro Render-Pass die Hex-Konnektivitaet pro Spieler ausrechnen.
        // Felder, die nicht in dieser Menge stehen, gelten visuell als tot.
        val connectedByPlayer: Map<String, Set<Pair<Int, Int>>> =
            players.associate { player ->
                player.name to FieldConnectivity.connectedFields(fields, units, player.name)
            }

        for ((col, row) in HexGridLogic.allCells(layout)) {
            val (cx, cy) = HexGridLogic.cellCenter(col, row, layout)

            fieldsByPosition[col to row]?.let { field ->
                field.owner?.let { owner ->
                    val cellPos = col to row
                    val unitHere = unitsByPosition[cellPos]
                    val ownerSkeletonOnField = unitHere != null &&
                            unitHere.player == owner &&
                            unitHere.type == UnitType.SKELETON
                    val notConnectedToBase =
                        cellPos !in (connectedByPlayer[owner] ?: emptySet())
                    val showAsDead = field.isSkeleton ||
                            ownerSkeletonOnField ||
                            notConnectedToBase
                    val fillColor = if (showAsDead) {
                        PlayerColorMap.skeletonCellFillFor(owner, players)
                    } else {
                        PlayerColorMap.cellFillFor(owner, players)
                    }
                    drawCellFill(cx, cy, layout.hexSize, fillColor)
                }
            }

            drawHex(cx, cy, layout.hexSize)

            buildingsByPosition[col to row]?.let { building ->
                val player = playerMap[building.player]
                val color = player?.color ?: PlayerColor.RED
                drawBuilding(building, color, buildingPainters, cx, cy, layout.hexSize)
            }

            unitsByPosition[col to row]?.let { unit ->
                val player = playerMap[unit.player]
                val color = player?.color ?: PlayerColor.RED
                drawUnit(unit, color, unitPainters, cx, cy, layout.hexSize)
            }
        }

        if (darkenedCells.isNotEmpty()) {
            for ((col, row) in darkenedCells) {
                val (cx, cy) = HexGridLogic.cellCenter(col, row, layout)
                drawCellFill(cx, cy, layout.hexSize, Color(0f, 0f, 0f, 0.45f))
            }
        }
    }

    private fun DrawScope.drawCellFill(cx: Float, cy: Float, size: Float, color: Color) {
        val path = Path()
        HexGridLogic.hexCorners(cx, cy, size).forEachIndexed { i, (x, y) ->
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        drawPath(path, color, style = Fill)
    }

    private fun DrawScope.drawHex(cx: Float, cy: Float, size: Float) {
        val path = Path()
        HexGridLogic.hexCorners(cx, cy, size).forEachIndexed { i, (x, y) ->
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        drawPath(path, Color.Black, style = Stroke(3f))
    }

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

    private fun DrawScope.drawBuilding(
        building: Building,
        color: PlayerColor,
        painters: Map<Pair<PlayerColor, BuildingType>, Painter>,
        cx: Float,
        cy: Float,
        size: Float
    ) {
        val painter = painters[color to building.type] ?: return
        val iconSize = size * 1.5f

        translate(cx - iconSize / 2, cy - iconSize / 2) {
            with(painter) {
                draw(size = Size(iconSize, iconSize))
            }
        }
    }
}