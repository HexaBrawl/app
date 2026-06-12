package at.aau.serg.websocketbrokerdemo.ui.game.tophud.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import at.aau.serg.websocketbrokerdemo.ui.game.tophud.IncomeBreakdown
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinDark
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBlack
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentDark
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentLight
import com.example.myapplication.R

/**
 * Detail-Popup fuers Einkommen pro Runde.
 *
 * Reine Anzeige-Komponente. Bekommt den View-State [IncomeBreakdown]
 * von oben (gebaut in [at.aau.serg.websocketbrokerdemo.ui.game.tophud
 * .IncomeBreakdownLogic.buildBreakdown]) und ruft [onDismiss] beim
 * Schliessen auf. Keine Berechnung, kein Server-Zugriff.
 *
 * Aufbau (wie im Mockup): Drei Aufschluesselungs-Bloecke (Farmen,
 * Felder, Truppenunterhalt) jeweils mit Header-Zeile (Kategorie-Name)
 * und Body-Zeile (Formel + Wert). Dazwischen die Summen-Zeilen
 * "Einkommen brutto" und "Netto pro Runde" als fette Einzeiler.
 */
@Composable
fun IncomeDetailsPopup(
    breakdown: IncomeBreakdown,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .shadow(20.dp, RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.verticalGradient(listOf(ParchmentLight, ParchmentDark)),
                    shape = RoundedCornerShape(16.dp)
                )
                .border(3.dp, GoldCoinDark, RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Text(
                text = stringResource(R.string.income_dialog_title),
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = InkBlack,
                    letterSpacing = 1.5.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            IncomeSectionHeader(label = stringResource(R.string.income_farms))
            IncomeRow(
                label = "${breakdown.farms} x ${breakdown.goldPerFarm} ${stringResource(R.string.income_gold)}",
                value = "= +${breakdown.farmIncome}",
                modifier = Modifier.padding(start = 12.dp)
            )

            Spacer(Modifier.height(4.dp))

            IncomeSectionHeader(label = stringResource(R.string.income_fields))
            IncomeRow(
                label = "${breakdown.fields} x ${breakdown.goldPerField} ${stringResource(R.string.income_gold)}",
                value = "= +${breakdown.fieldIncome}",
                modifier = Modifier.padding(start = 12.dp)
            )

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(thickness = 1.dp, color = GoldCoinDark)
            Spacer(Modifier.height(8.dp))

            IncomeRow(
                label = stringResource(R.string.income_gross),
                value = "+${breakdown.grossIncome}",
                bold = true
            )

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(thickness = 1.dp, color = GoldCoinDark)
            Spacer(Modifier.height(8.dp))

            IncomeSectionHeader(label = stringResource(R.string.income_upkeep))
            val upkeepFormula = upkeepFormulaString(breakdown.units)
            val troopsLabel = stringResource(R.string.income_troops_with_count, breakdown.units)
            IncomeRow(
                label = "$troopsLabel $upkeepFormula".trim(),
                value = "-${breakdown.upkeep}",
                modifier = Modifier.padding(start = 12.dp)
            )

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(thickness = 1.dp, color = GoldCoinDark)
            Spacer(Modifier.height(8.dp))

            IncomeRow(
                label = stringResource(R.string.income_net),
                value = formatSigned(breakdown.netIncome),
                bold = true
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldCoinDark,
                    contentColor = InkBlack
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.dialog_close),
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

/**
 * Baut den Anzeige-String fuer die Truppenunterhalt-Formel.
 * Beispiel: bei units=3 -> "(3+4+5)", bei units=1 -> "(3)",
 * bei units=0 -> "" (keine Klammer).
 */
private fun upkeepFormulaString(units: Int): String {
    if (units <= 0) return ""
    val terms = (0 until units).map { 3 + it }
    return "(${terms.joinToString("+")})"
}

/**
 * Formatiert eine Zahl mit Vorzeichen: 5 -> "+5", -2 -> "-2", 0 -> "0".
 */
private fun formatSigned(value: Int): String = when {
    value > 0 -> "+$value"
    else -> "$value"
}
