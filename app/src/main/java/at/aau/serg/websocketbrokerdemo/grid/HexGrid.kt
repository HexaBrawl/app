package at.aau.serg.websocketbrokerdemo.grid

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import at.aau.serg.websocketbrokerdemo.data.serverside.Building
import at.aau.serg.websocketbrokerdemo.data.serverside.BuildingType
import at.aau.serg.websocketbrokerdemo.data.serverside.Field
import at.aau.serg.websocketbrokerdemo.data.serverside.GameUnit
import at.aau.serg.websocketbrokerdemo.data.serverside.Player
import at.aau.serg.websocketbrokerdemo.data.serverside.PlayerColor
import at.aau.serg.websocketbrokerdemo.data.serverside.UnitType

/**
 * Composable, das ein Hex-Grid samt Einheiten und Gebaeuden rendert.
 *
 * @param layout  Hex-Geometrie der aktuellen Karte
 * @param units   Liste der Einheiten (GameUnit) fuers Rendering
 * @param buildings Liste der Gebaeude (Building) fuers Rendering
 * @param fields  Hex-Felder mit Besitzer-Info fuer die Einfaerbung
 *                eroberter Zellen (subissue #123)
 * @param players Volle Spieler-Liste fuer das Farb-Mapping
 */
@Composable
fun HexGrid(
    layout: MapLayout,
    units: List<GameUnit>,
    buildings: List<Building>,
    fields: List<Field>,
    players: List<Player>,
    darkenedCells: Set<Pair<Int, Int>> = emptySet(),
    highlightedCells: Set<Pair<Int, Int>> = emptySet(),
    modifier: Modifier = Modifier
) {
    val renderer = remember { HexRenderer() }

    // Resolve painters for all possible unit combinations
    val unitPainters = mutableMapOf<Pair<PlayerColor, UnitType>, Painter>()
    PlayerColor.entries.forEach { color ->
        UnitType.entries.forEach { type ->
            unitPainters[color to type] = painterResource(id = UnitIconProvider.iconFor(color, type))
        }
    }

    // Resolve painters for buildings
    val buildingPainters = mutableMapOf<Pair<PlayerColor, BuildingType>, Painter>()
    PlayerColor.entries.forEach { color ->
        BuildingType.entries.forEach { type ->
            val iconId = BuildingIconProvider.iconFor(color, type)
            if (iconId != 0) {
                buildingPainters[color to type] = painterResource(id = iconId)
            }
        }
    }

    Canvas(modifier = modifier) {
        with(renderer) {
            render(
                layout, units, buildings, fields, players,
                unitPainters, buildingPainters, darkenedCells, highlightedCells
            )
        }
    }
}
