package at.aau.serg.websocketbrokerdemo.grid

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import at.aau.serg.websocketbrokerdemo.data.serverside.GameUnit
import at.aau.serg.websocketbrokerdemo.data.serverside.Player

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

    Canvas(modifier = modifier) {
        with(renderer) {
            render(layout, units, players)
        }
    }
}
