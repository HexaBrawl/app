package at.aau.serg.websocketbrokerdemo.ui.settings.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBlack

@Composable
internal fun SettingsSectionTitle(text: String) {
    Text(
        text = text,
        style = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = InkBlack,
            letterSpacing = 1.sp
        )
    )
}