package at.aau.serg.websocketbrokerdemo.ui.game.tophud.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import at.aau.serg.websocketbrokerdemo.ui.game.LocalHudSizing
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinDark
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBlack
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentDark
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentLight

/**
 * Burger-Menue-Button rechts im Top-HUD.
 */
@Composable
fun HudMenuButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sizing = LocalHudSizing.current

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(sizing.topMenuButtonSize)
            .shadow(4.dp, RoundedCornerShape(10.dp))
            .background(
                brush = Brush.verticalGradient(listOf(ParchmentLight, ParchmentDark)),
                shape = RoundedCornerShape(10.dp)
            )
            .border(2.5.dp, GoldCoinDark, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = Icons.Filled.Menu,
            contentDescription = null,
            tint = InkBlack,
            modifier = Modifier.size(sizing.topMenuIconSize)
        )
    }
}