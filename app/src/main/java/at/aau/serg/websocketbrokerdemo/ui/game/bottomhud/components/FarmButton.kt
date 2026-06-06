package at.aau.serg.websocketbrokerdemo.ui.game.bottomhud.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import at.aau.serg.websocketbrokerdemo.ui.game.LocalHudSizing
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBlack
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentDark
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentLight
import com.example.myapplication.R

/**
 * "Farm kaufen"-Knopf links im Bottom-HUD.
 *
 * Zeigt das Farm-Icon, einen Preis-Badge oben rechts (der Preis steigt
 * dynamisch mit der Anzahl bereits gekaufter Farms) und ein Label
 * darunter. Wird grayed out wenn der Spieler sich keine Farm leisten
 * kann oder nicht am Zug ist.
 */
@Composable
fun FarmButton(
    enabled: Boolean,
    price: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sizing = LocalHudSizing.current

    val baseModifier = modifier
        .alpha(if (enabled) 1f else 0.4f)
        .padding(
            horizontal = sizing.bottomItemHorizontalPadding,
            vertical = sizing.bottomItemVerticalPadding
        )

    val finalModifier = if (enabled) baseModifier.clickable(onClick = onClick) else baseModifier

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = finalModifier
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Image(
                painter = painterResource(id = R.drawable.farm_icon),
                contentDescription = null,
                modifier = Modifier.size(sizing.bottomIconSize)
            )

            // Preis oben rechts
            Box(
                modifier = Modifier
                    .background(ParchmentDark, RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = price.toString(),
                    style = TextStyle(
                        fontSize = sizing.bottomFarmPriceFontSize,
                        fontWeight = FontWeight.Bold,
                        color = InkBlack
                    )
                )
            }
        }
        Text(
            text = stringResource(R.string.bottom_hud_buy_farm),
            style = TextStyle(
                fontSize = sizing.bottomTitleFontSize,
                fontWeight = FontWeight.ExtraBold,
                color = ParchmentLight
            )
        )
    }
}