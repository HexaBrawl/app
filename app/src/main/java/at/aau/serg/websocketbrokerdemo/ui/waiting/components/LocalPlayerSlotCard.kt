package at.aau.serg.websocketbrokerdemo.ui.waiting.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.aau.serg.websocketbrokerdemo.data.serverside.PlayerColor
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinDark
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBlack
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBrown
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentDark
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentLight
import at.aau.serg.websocketbrokerdemo.ui.theme.WoodMedium
import at.aau.serg.websocketbrokerdemo.ui.waiting.model.PlayerSlot
import com.example.myapplication.R

/**
 * Karte fuer den lokalen Spieler (eigenes Geraet).
 *
 * Erlaubt das Aendern von Name, Farbe und Bereit-Status. Sobald der
 * Spieler "bereit" tippt, werden die Eingabefelder gesperrt und die
 * Farbsiegel ausgegraut.
 */
@Composable
fun LocalPlayerSlotCard(
    slot: PlayerSlot,
    takenColors: Set<PlayerColor>,
    countdownActive: Boolean,
    joinedServer: Boolean,
    onNameChange: (String) -> Unit,
    onColorChange: (PlayerColor) -> Unit,
    onReadyToggle: () -> Unit
) {
    // Waehrend des 3-2-1-Countdowns ist der "Bereit"-Button gesperrt.
    // Der User kann sein "Bereit" nicht mehr zuruecknehmen, sobald der
    // Spielstart unmittelbar bevorsteht -- so vermeiden wir, dass er
    // im Spiel landet, obwohl er kurz vorher noch "nicht bereit"
    // geklickt hatte.
    val canBeReady = slot.name.isNotBlank() && !countdownActive

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(14.dp))
            .background(
                brush = Brush.verticalGradient(listOf(ParchmentLight, ParchmentDark)),
                shape = RoundedCornerShape(14.dp)
            )
            .border(3.dp, GoldCoinDark, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        brush = Brush.radialGradient(listOf(slot.color.main, slot.color.dark)),
                        shape = CircleShape
                    )
                    .border(2.dp, GoldCoinDark, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = ParchmentLight,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.waiting_your_orders),
                style = TextStyle(
                    fontSize = 13.sp,
                    color = InkBrown,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                )
            )
        }

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = slot.name,
            onValueChange = onNameChange,
            singleLine = true,
            enabled = !slot.ready && !joinedServer,
            keyboardOptions = KeyboardOptions.Default,
            label = {
                Text(stringResource(R.string.waiting_your_name), style = TextStyle(fontSize = 12.sp))
            },
            leadingIcon = {
                Icon(Icons.Filled.Edit, contentDescription = null, tint = InkBrown)
            },
            textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = InkBlack),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GoldCoinDark,
                unfocusedBorderColor = WoodMedium,
                disabledBorderColor = WoodMedium,
                focusedContainerColor = ParchmentLight,
                unfocusedContainerColor = ParchmentLight,
                disabledContainerColor = ParchmentLight,
                focusedLabelColor = InkBrown,
                unfocusedLabelColor = InkBrown,
                disabledTextColor = InkBlack,
                cursorColor = InkBlack
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(14.dp))

        Text(
            text = stringResource(R.string.waiting_choose_color),
            style = TextStyle(
                fontSize = 12.sp,
                color = InkBrown,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        )
        Spacer(Modifier.height(6.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            PlayerColor.entries.forEach { color ->
                ColorSeal(
                    color = color,
                    selected = slot.color == color,
                    disabled = color in takenColors || slot.ready || joinedServer,
                    onClick = { onColorChange(color) }
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        ReadyButton(
            ready = slot.ready,
            enabled = canBeReady,
            onClick = onReadyToggle,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
