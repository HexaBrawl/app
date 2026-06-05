package at.aau.serg.websocketbrokerdemo.ui.game.bottomhud.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentLight
import com.example.myapplication.R

/**
 * "Runde aus"-Knopf rechts im Bottom-HUD.
 *
 * Disabled wenn nicht der lokale Spieler am Zug ist.
 */
@Composable
fun EndTurnButton(
    enabled: Boolean,
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
        Image(
            painter = painterResource(id = R.drawable.hourglass_icon),
            contentDescription = null,
            modifier = Modifier.size(50.dp)
        )
        Spacer(Modifier.height(5.dp))
        Text(
            text = stringResource(R.string.bottom_hud_end_turn),
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ParchmentLight
            )
        )
    }
}
