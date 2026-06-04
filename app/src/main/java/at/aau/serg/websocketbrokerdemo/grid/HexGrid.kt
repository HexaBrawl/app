package at.aau.serg.websocketbrokerdemo.grid

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import at.aau.serg.websocketbrokerdemo.data.serverside.GameUnit
import at.aau.serg.websocketbrokerdemo.data.serverside.Player
import at.aau.serg.websocketbrokerdemo.data.serverside.PlayerColor
import at.aau.serg.websocketbrokerdemo.data.serverside.UnitType

/**
 * Composable, das ein Hex-Grid samt Einheiten rendert.
 *
 * @param layout  Hex-Geometrie der aktuellen Karte
 * @param units   Liste der Einheiten (GameUnit) fuers Rendering
 * @param players Volle Spieler-Liste fuer das Farb-Mapping
 */
@Composable
fun HexGrid(
    layout: MapLayout,
    units: List<GameUnit>,
    players: List<Player>,
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

    Canvas(modifier = modifier) {
        with(renderer) {
            render(layout, units, players, unitPainters)
        }
    }
}
