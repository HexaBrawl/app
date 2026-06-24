package at.aau.serg.websocketbrokerdemo.ui.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinDark
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinLight
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBlack
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentLight
import at.aau.serg.websocketbrokerdemo.ui.theme.WoodDark
import at.aau.serg.websocketbrokerdemo.ui.theme.WoodMedium

/**
 * Beschriftete Umschalt-Option (Label links, Switch rechts) im Settings-Screen.
 *
 * Genereller Baustein fuer An/Aus-Einstellungen wie Musik oder Soundeffekte.
 * Haelt selbst keinen State -- der [checked]-Wert kommt von aussen rein,
 * Aenderungen gehen ueber [onCheckedChange] zurueck.
 *
 * @param label           Beschriftung der Option.
 * @param checked         Aktueller An/Aus-Zustand.
 * @param onCheckedChange Callback bei Umschalten, liefert den neuen Wert.
 */

@Composable
internal fun SettingsOptionSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = TextStyle(fontSize = 18.sp, color = InkBlack, fontWeight = FontWeight.SemiBold)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = GoldCoinLight,
                checkedTrackColor = GoldCoinDark,
                uncheckedThumbColor = ParchmentLight,
                uncheckedTrackColor = WoodMedium,
                checkedBorderColor = GoldCoinDark,
                uncheckedBorderColor = WoodDark
            )
        )
    }
}