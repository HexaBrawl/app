package at.aau.serg.websocketbrokerdemo.ui.game.tophud.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
 * Vollbild-Overlay fuer die uebrigen Spieler, wenn ein Mitspieler die
 * WebSocket-Verbindung verloren hat.
 *
 * Zeigt die Namen aller disconnecteten Spieler (Komma-separiert) sowie
 * einen Wartebildschirm "Warte auf Reconnect…". Verschwindet automatisch
 *  - sobald der disconnectete Spieler wieder connected ist (server
 *    setzt Player.connected zurueck auf true), oder
 *  - der Server den Spieler nach Ablauf der Grace-Period (~30s) aus
 *    state.players entfernt hat — dann ist [disconnectedNames] leer
 *    und der Overlay wird vom Aufrufer gar nicht erst gerendert.
 *
 * Schluckt alle Taps, damit das Spielfeld waehrend des Wartens nicht
 * versehentlich bedient werden kann.
 */
@Composable
fun DisconnectedPlayerOverlay(
    disconnectedNames: List<String>
) {
    if (disconnectedNames.isEmpty()) return
    val joined = disconnectedNames.joinToString(", ")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .pointerInput(Unit) { detectTapGestures { /* absorb */ } },
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
            Text(
                text = stringResource(R.string.disconnect_overlay_title, joined),
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = InkBlack,
                    textAlign = TextAlign.Center
                )
            )
            Spacer(Modifier.height(16.dp))
            CircularProgressIndicator(color = GoldCoinDark)
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.disconnect_overlay_subtitle),
                style = TextStyle(
                    fontSize = 13.sp,
                    color = InkBrown,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}
