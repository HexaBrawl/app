package at.aau.serg.websocketbrokerdemo.ui.game.bottomhud

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import at.aau.serg.websocketbrokerdemo.data.serverside.Building
import at.aau.serg.websocketbrokerdemo.data.serverside.GameStatus
import at.aau.serg.websocketbrokerdemo.data.serverside.Player
import at.aau.serg.websocketbrokerdemo.data.serverside.UnitType
import at.aau.serg.websocketbrokerdemo.ui.game.GameHudSizing
import at.aau.serg.websocketbrokerdemo.ui.game.LocalHudSizing
import at.aau.serg.websocketbrokerdemo.ui.game.GameHudSizingLogic

/**
 * Bottom-HUD des GameScreens.
 *
 * Dunkler Holzbalken ueber die ganze Breite. Inhalt:
 *  - Links:  Farm-kaufen-Knopf mit dynamisch berechnetem Preis
 *  - Mitte:  Drei Truppen-Muenzen (Infanterie, Bogenschuetze, Kavallerie)
 *  - Rechts: Runde-aus-Knopf
 *
 * Skaliert sich automatisch mit der Bildschirmbreite ueber [HudSizing] +
 * [GameHudSizingLogic]. Sub-Components lesen die Werte aus dem
 * CompositionLocal -- keine durchgereichten Sizing-Parameter noetig.
 */
@Composable
fun BottomHud(
    players: List<Player>,
    buildings: List<Building>,
    localName: String?,
    currentTurn: String?,
    status: GameStatus,
    placementMode: UnitType?,
    onBuyFarm: () -> Unit,
    onSelectUnit: (UnitType) -> Unit,
    onEndTurn: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val sizing = remember(maxWidth) { GameHudSizingLogic.forWidth(maxWidth.value) }

        CompositionLocalProvider(LocalHudSizing provides sizing) {
            BottomHudContent(
                players = players,
                buildings = buildings,
                localName = localName,
                currentTurn = currentTurn,
                status = status,
                placementMode = placementMode,
                onBuyFarm = onBuyFarm,
                onSelectUnit = onSelectUnit,
                onEndTurn = onEndTurn
            )
        }
    }
}