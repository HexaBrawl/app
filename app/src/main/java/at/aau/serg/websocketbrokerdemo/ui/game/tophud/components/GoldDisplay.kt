package at.aau.serg.websocketbrokerdemo.ui.game.tophud.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import at.aau.serg.websocketbrokerdemo.ui.game.LocalHudSizing
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinLight
import at.aau.serg.websocketbrokerdemo.ui.theme.WoodDark
import com.example.myapplication.R

/**
 * Gold-Anzeige links im Top-HUD.
 *
 * Kompakte dunkle Box: Muenze und Zahl haben den gleichen Abstand zum
 * Box-Rand und sitzen nah beieinander.
 */
@Composable
fun GoldDisplay(
    gold: Int,
    modifier: Modifier = Modifier
) {
    val sizing = LocalHudSizing.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(10.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF2A1A0A), WoodDark)
                ),
                shape = RoundedCornerShape(10.dp)
            )
            .border(2.dp, GoldCoinLight, RoundedCornerShape(10.dp))
            .padding(
                horizontal = 8.dp,
                vertical = sizing.topBoxVerticalPadding
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.gold_coin),
            contentDescription = null,
            modifier = Modifier.size(sizing.topGoldIconSize)
        )
        Spacer(Modifier.width(sizing.topInnerSpacer))
        Text(
            text = gold.toString(),
            style = TextStyle(
                fontSize = sizing.topGoldFontSize,
                fontWeight = FontWeight.ExtraBold,
                color = GoldCoinLight
            )
        )
    }
}