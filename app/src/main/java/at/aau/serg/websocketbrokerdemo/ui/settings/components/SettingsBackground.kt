package at.aau.serg.websocketbrokerdemo.ui.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinDark
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentDark
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentLight

/**
 * Pergament-Karte als Hintergrund-Wrapper fuer Settings-Inhalte.
 *
 * Legt den uebergebenen [content] in eine Box mit Pergament-Verlauf,
 * Gold-Rand und Schatten und ordnet ihn vertikal in einer Column an.
 * Dient als einheitlicher Rahmen, damit alle Settings-Abschnitte gleich
 * aussehen.
 *
 * @param content Die im Pergament-Rahmen dargestellten Composables.
 */

@Composable
internal fun SettingsBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .background(
                brush = Brush.verticalGradient(listOf(ParchmentLight, ParchmentDark)),
                shape = RoundedCornerShape(12.dp)
            )
            .border(2.dp, GoldCoinDark, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column { content() }
    }
}