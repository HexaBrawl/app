package at.aau.serg.websocketbrokerdemo.ui.game.tophud.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBlack

/**
 * Eine Zeile im Einkommen-Detail-Popup.
 *
 * Label links, Wert rechts -- konsistent gestaltet, damit alle
 * Zeilen (Farmen, Felder, Brutto, Truppenunterhalt, Netto) gleich
 * aussehen.
 *
 *  - [label]  Klartext links, z.B. "Farmen" oder "Einkommen brutto".
 *  - [value]  Anzuzeigender Wert rechts, z.B. "+6" oder "-12".
 *  - [bold]   Wenn true wird die Zeile extrabold gerendert -- gedacht
 *             fuer Summen-Zeilen wie "Einkommen brutto" und "Netto".
 */
@Composable
fun IncomeRow(
    label: String,
    value: String,
    bold: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = if (bold) FontWeight.ExtraBold else FontWeight.Medium,
                color = InkBlack
            )
        )
        Text(
            text = value,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = if (bold) FontWeight.ExtraBold else FontWeight.Bold,
                color = InkBlack
            )
        )
    }
}
