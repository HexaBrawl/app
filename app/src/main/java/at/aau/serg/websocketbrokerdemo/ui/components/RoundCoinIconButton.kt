package at.aau.serg.websocketbrokerdemo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinDark
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinLight
import at.aau.serg.websocketbrokerdemo.ui.theme.WoodDark
import at.aau.serg.websocketbrokerdemo.ui.theme.WoodLight

/**
 * Runder Holz-mit-Goldrand-Button im Wachs-Siegel-Stil.
 *
 * Wird in der Top-Bar von MainMenu, LobbyScreen und WaitingLobby
 * fuer Zurueck-/Info-Aktionen verwendet. Hier zentral als
 * wiederverwendbarer Composable, damit das Design konsistent bleibt
 * und nicht in jedem Screen erneut definiert werden muss.
 */
@Composable
fun RoundCoinIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(48.dp)
            .shadow(6.dp, CircleShape)
            .background(
                brush = Brush.radialGradient(listOf(WoodLight, WoodDark)),
                shape = CircleShape
            )
            .border(2.dp, GoldCoinDark, CircleShape)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = GoldCoinLight
        )
    }
}
