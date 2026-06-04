package at.aau.serg.websocketbrokerdemo.ui.game.tophud.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.window.Dialog
import at.aau.serg.websocketbrokerdemo.ui.mainmenu.components.DialogButton
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinDark
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBlack
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBrown
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentDark
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentLight
import com.example.myapplication.R

/**
 * Popup beim Gegner: "Spieler X hat ein Geschenk geoeffnet - stehlen?"
 *
 * Zeigt absichtlich NICHT das Delta -- der Stealer muss blind
 * entscheiden, das ist Teil des Schummel-Gambles. Er weiss nicht ob
 * er Gold gewinnt oder ins Minus rutscht.
 *
 * Nicht-dismissbar per Klick aussen -- der Spieler muss eine
 * Entscheidung treffen, sonst blockt das Spiel.
 */
@Composable
fun StealPopup(
    ownerName: String,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Dialog(onDismissRequest = { /* nicht-dismissable */ }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(20.dp, RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.verticalGradient(listOf(ParchmentLight, ParchmentDark)),
                    shape = RoundedCornerShape(16.dp)
                )
                .border(3.dp, GoldCoinDark, RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Text(
                text = stringResource(R.string.cheat_steal_title),
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = InkBlack
                )
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.cheat_steal_body, ownerName),
                style = TextStyle(
                    fontSize = 14.sp,
                    color = InkBrown
                )
            )

            Spacer(Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DialogButton(
                    text = stringResource(R.string.cheat_steal_no),
                    primary = false,
                    onClick = onDecline
                )
                DialogButton(
                    text = stringResource(R.string.cheat_steal_yes),
                    primary = true,
                    onClick = onAccept
                )
            }
        }
    }
}
