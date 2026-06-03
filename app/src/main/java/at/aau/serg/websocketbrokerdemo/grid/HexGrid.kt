package at.aau.serg.websocketbrokerdemo.grid

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

/**
 * Composable, das ein Hex-Grid samt Einheiten rendert.
 *
 * Vorher: UniversalGrid (Composable) + UniversalGridLogic (Tap-Math)
 *         + HexInput (delegierte an Layout) + HexLayout (Mathematik).
 *         Vier Files fuer "zeichne ein Grid und gib Taps weiter".
 *
 * Jetzt: dieser Composable plus [HexGridLogic] -- fertig. Tap-Handling
 * ist absichtlich aus dem Composable raus, weil der aussenliegende
 * Container (siehe GameMap) die Taps auf einem viel groesseren Bereich
 * abfaengt und ueber HexGridLogic.pixelToCell selbst aufloest.
 */
@Composable
fun HexGrid(
    layout: MapLayout,
    units: List<UnitData>,
    modifier: Modifier = Modifier
) {
    val renderer = remember { HexRenderer() }

    Canvas(modifier = modifier) {
        with(renderer) {
            render(layout, units)
        }
    }
}
