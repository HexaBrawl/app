package at.aau.serg.websocketbrokerdemo.ui.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinDark
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinLight
import at.aau.serg.websocketbrokerdemo.ui.theme.WoodDark
import at.aau.serg.websocketbrokerdemo.ui.theme.WoodLight
import com.example.myapplication.R

/**
 * Runder Zurueck-Button im Settings-Screen.
 *
 * Stilisiert als Holz-Medaillon mit Gold-Rand, passend zum Parchment-Theme
 * des Spiels. Die contentDescription kommt aus den String-Resources, damit
 * Screenreader den Button korrekt vorlesen.
 *
 * @param onClick Callback beim Antippen (typischerweise Navigation zurueck).
 */

@Composable
internal fun SettingsBackButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(48.dp)
            .shadow(4.dp, CircleShape)
            .background(
                brush = Brush.radialGradient(listOf(WoodLight, WoodDark)),
                shape = CircleShape
            )
            .border(2.dp, GoldCoinDark, CircleShape)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.settings_back),
            tint = GoldCoinLight
        )
    }
}