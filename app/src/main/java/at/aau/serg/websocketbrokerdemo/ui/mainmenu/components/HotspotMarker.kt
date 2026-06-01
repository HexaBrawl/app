package at.aau.serg.websocketbrokerdemo.ui.mainmenu.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import at.aau.serg.websocketbrokerdemo.ui.mainmenu.HotspotCalculator

/**
 * Unsichtbarer (eigentlich dezent rot-glühender) Touch-Hotspot über einem
 * X auf der Übersichtskarte.
 *
 *  - Position über Prozent (xPct/yPct), berechnet von HotspotCalculator
 *  - Pulsiert sanft, damit der Spieler ihn entdeckt
 *  - Ruft beim Tap die übergebene onTap-Lambda
 */
@Composable
fun HotspotMarker(
    xPct: Float,
    yPct: Float,
    parentW: Dp,
    parentH: Dp,
    onTap: () -> Unit
) {
    val (cx, cy) = HotspotCalculator.computeCenter(
        parentW = parentW.value,
        parentH = parentH.value,
        xPct = xPct,
        yPct = yPct
    )

    val centerX = cx.dp
    val centerY = cy.dp

    val infinite = rememberInfiniteTransition(label = "hotspotPulse")
    val pulse by infinite.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hotspotPulseScale"
    )

    val hotspotSize = 96.dp
    Box(
        modifier = Modifier
            .offset(
                x = centerX - hotspotSize / 2,
                y = centerY - hotspotSize / 2
            )
            .size(hotspotSize)
            .scale(pulse)
            .clickable(onClick = onTap)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x66FF3030),
                        Color(0x22FF3030),
                        Color.Transparent
                    )
                ),
                shape = CircleShape
            )
    )
}
