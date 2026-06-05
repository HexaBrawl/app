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
import androidx.compose.ui.unit.sp
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBlack
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentDark
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentLight
import com.example.myapplication.R

/**
 * "Farm kaufen"-Pergament-Knopf links im Bottom-HUD.
 *
 * Wird grayed out wenn der Spieler sich keine Farm leisten kann oder
 * nicht am Zug ist.
 */
@Composable
fun FarmButton(
    enabled: Boolean,
    price: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val baseModifier = modifier
        .alpha(if (enabled) 1f else 0.4f)
        .padding(horizontal = 10.dp, vertical = 6.dp)

    val finalModifier = if (enabled) baseModifier.clickable(onClick = onClick) else baseModifier

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = finalModifier
    ) {
        Box(
            contentAlignment = Alignment.TopEnd
        ) {
            Image(
                painter = painterResource(id = R.drawable.farm_icon),
                contentDescription = null,
                modifier = Modifier.size(60.dp)
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
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = InkBlack
                    )
                )
            }
        }
        Text(
            text = stringResource(R.string.bottom_hud_buy_farm),
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ParchmentLight
            )
        )
    }
}
