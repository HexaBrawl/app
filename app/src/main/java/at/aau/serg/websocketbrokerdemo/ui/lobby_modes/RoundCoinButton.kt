package at.aau.serg.websocketbrokerdemo.ui.lobby_modes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import at.aau.serg.websocketbrokerdemo.ui.theme.*

fun Modifier.roundCoinButton(): Modifier = this
    .size(48.dp)
    .shadow(6.dp, CircleShape)
    .background(
        brush = Brush.radialGradient(listOf(WoodLight, WoodDark)),
        shape = CircleShape
    )
    .border(2.dp, GoldCoinDark, CircleShape)
