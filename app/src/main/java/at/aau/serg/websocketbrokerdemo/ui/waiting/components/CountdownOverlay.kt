package at.aau.serg.websocketbrokerdemo.ui.waiting.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoin
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinDark
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinLight
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBlack

/**
 * Vollbild-Overlay mit grosser Countdown-Zahl.
 *
 * Wird vom Screen ueber AnimatedVisibility ein-/ausgeblendet, sobald
 * der Countdown laeuft. Goldene Wachssiegel-Optik mit Sanduhr-Icon.
 */
@Composable
fun CountdownOverlay(seconds: Int) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .wrapContentSize()
            .shadow(20.dp, CircleShape)
            .background(
                brush = Brush.radialGradient(listOf(GoldCoinLight, GoldCoin, GoldCoinDark)),
                shape = CircleShape
            )
            .border(4.dp, GoldCoinDark, CircleShape)
            .padding(40.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Filled.HourglassBottom,
                contentDescription = null,
                tint = InkBlack,
                modifier = Modifier.size(40.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = seconds.toString(),
                style = TextStyle(
                    fontSize = 60.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = InkBlack
                )
            )
        }
    }
}
