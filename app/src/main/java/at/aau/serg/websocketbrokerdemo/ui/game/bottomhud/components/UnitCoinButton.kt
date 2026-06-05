package at.aau.serg.websocketbrokerdemo.ui.game.bottomhud.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.aau.serg.websocketbrokerdemo.data.serverside.PlayerColor
import at.aau.serg.websocketbrokerdemo.data.serverside.UnitType
import at.aau.serg.websocketbrokerdemo.ui.game.bottomhud.UnitIconProvider
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinDark
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBlack
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentDark

/**
 * Truppen-Muenze im Bottom-HUD.
 *
 * Zeigt die Truppe in der Spielerfarbe an, hat einen Goldrand und einen
 * kleinen Preis-Badge unten dran. Wird grayed out wenn nicht leistbar
 * oder nicht am Zug.
 *
 * Klick = Platzierungs-Modus starten.
 */
@Composable
fun UnitCoinButton(
    type: UnitType,
    playerColor: PlayerColor,
    price: Int,
    enabled: Boolean,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(60.dp)
            .alpha(if (enabled) 1f else 0.4f)
            .clickable(enabled = enabled, onClick = onClick)
    ) {
        // Icon
        Image(
            painter = painterResource(UnitIconProvider.iconFor(playerColor, type)),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .size(60.dp)
        )

        // Preis oben rechts
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(2.dp)
                .background(ParchmentDark, RoundedCornerShape(4.dp))
                .padding(horizontal = 4.dp, vertical = 1.dp)
        ) {
            Text(
                text = price.toString(),
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = InkBlack
                )
            )
        }
    }
}
