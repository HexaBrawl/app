package at.aau.serg.websocketbrokerdemo.ui.waiting.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinDark
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinLight
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentLight
import at.aau.serg.websocketbrokerdemo.ui.waiting.model.PlayerColor

/**
 * Klickbares Farb-Wachssiegel zur Spielerfarbe-Auswahl.
 *
 * Drei Zustaende:
 *  - normal      antippbar, voll deckend
 *  - selected    goldener Rand + Haken-Icon
 *  - disabled    halbtransparent, nicht antippbar (Farbe schon belegt
 *                oder Spieler bereits ready)
 */
@Composable
fun ColorSeal(
    color: PlayerColor,
    selected: Boolean,
    disabled: Boolean,
    onClick: () -> Unit
) {
    val borderWidth = if (selected) 3.dp else 1.5.dp
    val borderColor = if (selected) GoldCoinLight else GoldCoinDark

    val baseModifier = Modifier
        .size(46.dp)
        .shadow(if (selected) 6.dp else 2.dp, CircleShape)
        .background(
            brush = Brush.radialGradient(
                colors = if (disabled && !selected) {
                    listOf(
                        color.main.copy(alpha = 0.35f),
                        color.dark.copy(alpha = 0.35f)
                    )
                } else {
                    listOf(color.main, color.dark)
                }
            ),
            shape = CircleShape
        )
        .border(borderWidth, borderColor, CircleShape)

    val finalModifier = if (disabled) baseModifier else baseModifier.clickable(onClick = onClick)

    Box(
        contentAlignment = Alignment.Center,
        modifier = finalModifier
    ) {
        if (selected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = ParchmentLight,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
