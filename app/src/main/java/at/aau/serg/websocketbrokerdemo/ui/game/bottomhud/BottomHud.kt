package at.aau.serg.websocketbrokerdemo.ui.game.bottomhud

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import at.aau.serg.websocketbrokerdemo.data.serverside.Building
import at.aau.serg.websocketbrokerdemo.data.serverside.GameStatus
import at.aau.serg.websocketbrokerdemo.data.serverside.Player
import at.aau.serg.websocketbrokerdemo.data.serverside.UnitType
import at.aau.serg.websocketbrokerdemo.ui.game.bottomhud.components.EndTurnButton
import at.aau.serg.websocketbrokerdemo.ui.game.bottomhud.components.FarmButton
import at.aau.serg.websocketbrokerdemo.ui.game.bottomhud.components.UnitCoinButton
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinLight
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentDark
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentLight
import at.aau.serg.websocketbrokerdemo.ui.theme.WoodDark
import at.aau.serg.websocketbrokerdemo.ui.theme.WoodMedium

/**
 * Bottom-HUD des GameScreens.
 *
 * Holzbalken ueber die ganze Breite. Inhalt:
 *  - Links:  Farm-kaufen-Knopf
 *  - Mitte:  Drei Truppen-Muenzen (Infanterie, Bogenschuetze, Kavallerie)
 *  - Rechts: Runde-aus-Knopf
 *
 * Stateless -- die ganze Game-Logik (placementMode, isMyTurn) wird
 * von aussen reingereicht. So bleibt der HUD-Composable rein
 * darstellend und vom Game-State entkoppelt.
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
    val gold = BottomHudLogic.goldOf(players, localName)
    val playerColor = BottomHudLogic.colorOf(players, localName)
    val isMyTurn = BottomHudLogic.isMyTurn(currentTurn, status, localName)
    val farmPrice = BottomHudLogic.farmPrice(buildings, localName)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 50.dp, start = 3.dp, end = 3.dp)
            .height(100.dp)
            .shadow(10.dp, RoundedCornerShape(14.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF361B05),
                        Color(0xFF361B05)
                    )
                ),
                shape = RoundedCornerShape(14.dp)
            )
            .border(3.dp, GoldCoinLight, RoundedCornerShape(14.dp))
    ) {
        // Subtile Holzmaserung
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x22000000),
                            Color.Transparent,
                            Color(0x18000000),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(14.dp)
                )
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp)
        ) {
            FarmButton(
                enabled = BottomHudLogic.canBuyFarm(gold, isMyTurn, buildings, localName),
                price = farmPrice,
                onClick = onBuyFarm
            )

            Spacer(Modifier.weight(1f))

            UnitCoinButton(
                type = UnitType.INFANTRY,
                playerColor = playerColor,
                price = BottomHudLogic.priceOf(UnitType.INFANTRY),
                enabled = BottomHudLogic.canBuyUnit(UnitType.INFANTRY, gold, isMyTurn),
                selected = placementMode == UnitType.INFANTRY,
                onClick = { onSelectUnit(UnitType.INFANTRY) }
            )

            Spacer(Modifier.width(8.dp))

            UnitCoinButton(
                type = UnitType.ARCHER,
                playerColor = playerColor,
                price = BottomHudLogic.priceOf(UnitType.ARCHER),
                enabled = BottomHudLogic.canBuyUnit(UnitType.ARCHER, gold, isMyTurn),
                selected = placementMode == UnitType.ARCHER,
                onClick = { onSelectUnit(UnitType.ARCHER) }
            )

            Spacer(Modifier.width(8.dp))

            UnitCoinButton(
                type = UnitType.CAVALRY,
                playerColor = playerColor,
                price = BottomHudLogic.priceOf(UnitType.CAVALRY),
                enabled = BottomHudLogic.canBuyUnit(UnitType.CAVALRY, gold, isMyTurn),
                selected = placementMode == UnitType.CAVALRY,
                onClick = { onSelectUnit(UnitType.CAVALRY) }
            )

            Spacer(Modifier.weight(1f))

            EndTurnButton(
                enabled = BottomHudLogic.canEndTurn(isMyTurn),
                onClick = onEndTurn
            )
        }
    }
}
