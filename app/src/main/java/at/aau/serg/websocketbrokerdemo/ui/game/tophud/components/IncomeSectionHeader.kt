package at.aau.serg.websocketbrokerdemo.ui.game.tophud.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBlack

/**
 * Kleiner Header oberhalb einer Aufschluesselungs-Zeile im
 * Einkommen-Detail-Popup -- nur die Kategorie ("Farmen", "Felder",
 * "Truppenunterhalt") ohne Wert. Wird vom [IncomeDetailsPopup]
 * dreimal verwendet, damit die Bloecke visuell konsistent wirken.
 *
 *  - [label]  Kategorie-Name als Klartext.
 */
@Composable
fun IncomeSectionHeader(label: String) {
    Text(
        text = label,
        style = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = InkBlack
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
    )
}
