package at.aau.serg.websocketbrokerdemo.ui.waiting.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinDark
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinLight
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBrown
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentDark
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentLight
import at.aau.serg.websocketbrokerdemo.ui.theme.WoodDark
import at.aau.serg.websocketbrokerdemo.ui.theme.WoodLight
import com.example.myapplication.R

/**
 * Karte fuer einen leeren Slot ("Warte auf Verbuendeten...").
 *
 * Sanft pulsierender Rand + halbtransparente Pergament-Optik, damit
 * sich die Karte vom voll-besetzten Slot abhebt aber nicht aufdringlich
 * blinkt.
 */
@Composable
fun EmptySlotCard() {
    val infinite = rememberInfiniteTransition(label = "emptySlotPulse")
    val pulseAlpha by infinite.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
        label = "pulseAlpha"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(10.dp))
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        ParchmentLight.copy(alpha = 0.40f),
                        ParchmentDark.copy(alpha = 0.40f)
                    )
                ),
                shape = RoundedCornerShape(10.dp)
            )
            .border(
                1.5.dp,
                GoldCoinDark.copy(alpha = pulseAlpha * 0.6f),
                RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .background(
                    brush = Brush.radialGradient(
                        listOf(
                            WoodLight.copy(alpha = 0.6f),
                            WoodDark.copy(alpha = 0.6f)
                        )
                    ),
                    shape = CircleShape
                )
                .border(1.5.dp, GoldCoinDark.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Filled.PersonOutline,
                contentDescription = null,
                tint = GoldCoinLight.copy(alpha = pulseAlpha),
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = stringResource(R.string.waiting_empty_slot),
            style = TextStyle(
                fontSize = 14.sp,
                color = InkBrown.copy(alpha = pulseAlpha),
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Italic
            )
        )
    }
}
