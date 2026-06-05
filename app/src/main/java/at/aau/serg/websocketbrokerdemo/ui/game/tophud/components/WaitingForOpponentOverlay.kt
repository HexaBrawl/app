package at.aau.serg.websocketbrokerdemo.ui.game.tophud.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinDark
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBlack
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBrown
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentDark
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentLight
import com.example.myapplication.R

/**
 * Vollbild-Overlay fuer den Geschenk-Owner.
 *
 * Zeigt: "Du wuerdest +X Gold gewinnen, wirst du beklaut? Warte auf
 * Gegner..." Der Spieler kann nichts tun bis sein Geschenk entweder
 * geklaut oder durch alle Gegner abgelehnt wurde.
 *
 * Blockiert alle Taps darunter (Karte, HUD), damit der Spieler nicht
 * versehentlich Moves ausloesen kann waehrend das Spiel sowieso vom
 * Server blockiert ist.
 */
@Composable
fun WaitingForOpponentOverlay(
    delta: Int
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            // Alle Taps schlucken
            .pointerInput(Unit) {
                detectTapGestures { /* absorb */ }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(32.dp)
                .shadow(20.dp, RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.verticalGradient(listOf(ParchmentLight, ParchmentDark)),
                    shape = RoundedCornerShape(16.dp)
                )
                .border(3.dp, GoldCoinDark, RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            val title = if (delta >= 0) {
                stringResource(R.string.cheat_waiting_won, delta)
            } else {
                stringResource(R.string.cheat_waiting_lost, -delta)
            }

            Text(
                text = title,
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (delta >= 0) Color(0xFF2D7A1F) else Color(0xFF8B1A1A),
                    textAlign = TextAlign.Center
                )
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.cheat_waiting_subtitle),
                style = TextStyle(
                    fontSize = 14.sp,
                    color = InkBlack,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(Modifier.height(24.dp))

            CircularProgressIndicator(color = GoldCoinDark)

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.cheat_waiting_for_opponent),
                style = TextStyle(
                    fontSize = 13.sp,
                    color = InkBrown,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}
