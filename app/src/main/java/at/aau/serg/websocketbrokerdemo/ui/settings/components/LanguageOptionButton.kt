package at.aau.serg.websocketbrokerdemo.ui.settings.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoin
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinDark
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBlack
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentLight
import at.aau.serg.websocketbrokerdemo.ui.theme.WoodMedium

/**
 * Auswahl-Button fuer eine einzelne Sprache im Settings-Screen.
 *
 * Der ausgewaehlte Zustand wird rein optisch gespiegelt (Gold-Fuellung,
 * dickerer Rand, fetterer Text), damit der User die aktive Sprache auf
 * einen Blick erkennt. Die eigentliche Auswahl-Logik liegt beim Aufrufer.
 *
 * @param text     Anzeigetext des Buttons (z. B. der Sprachname).
 * @param selected Ob diese Sprache aktuell aktiv ist.
 * @param onClick  Callback beim Antippen.
 * @param modifier Optionaler Layout-Modifier.
 */

@Composable
internal fun LanguageOptionButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) GoldCoin else WoodMedium,
            contentColor = if (selected) InkBlack else ParchmentLight
        ),
        modifier = modifier
            .height(48.dp)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = GoldCoinDark,
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium
            )
        )
    }
}