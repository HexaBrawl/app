package at.aau.serg.websocketbrokerdemo.ui.waiting.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinDark
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentLight

/**
 * Kleines Bereit-/Wartet-Badge auf den Karten remoter Spieler.
 *
 * Gruener Haken wenn ready, grauer Sanduhr wenn nicht. Die Farbwerte
 * sind hartkodiert (statt aus dem Theme), damit der Badge unabhaengig
 * von der gewaehlten Spielerfarbe immer gleich aussieht.
 */
@Composable
fun ReadyBadge(ready: Boolean) {
    val main = if (ready) Color(0xFF2D5A1A) else Color(0xFF6B6B6B)
    val dark = if (ready) Color(0xFF1A3A0F) else Color(0xFF3A3A3A)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(40.dp)
            .shadow(3.dp, CircleShape)
            .background(brush = Brush.radialGradient(listOf(main, dark)), shape = CircleShape)
            .border(2.dp, GoldCoinDark, CircleShape)
    ) {
        Icon(
            imageVector = if (ready) Icons.Filled.Check else Icons.Filled.HourglassEmpty,
            contentDescription = null,
            tint = ParchmentLight,
            modifier = Modifier.size(20.dp)
        )
    }
}
