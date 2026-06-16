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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import at.aau.serg.websocketbrokerdemo.network.ConnectionState
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinDark
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinLight
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBlack
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBrown
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentDark
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentLight
import com.example.myapplication.R

/**
 * Vollbild-Overlay fuer den eigenen Spieler waehrend der App den
 * WebSocket wieder aufzubauen versucht.
 *
 * Zwei Modi:
 *  - [ConnectionState.Reconnecting]    Spinner + "Verbinde wieder…".
 *    Verschwindet automatisch sobald der naechste State-Broadcast vom
 *    Server reinkommt (also der Reconnect erfolgreich war).
 *  - [ConnectionState.LostPermanently] "Verbindung verloren"-Panel mit
 *    "Zurueck zum Menue"-Button. Wird angezeigt, wenn die Auto-Reconnect-
 *    Loop alle Versuche aufgebraucht hat (≈ 30s, matched die Server-
 *    Grace-Period).
 *
 * Schluckt alle Taps darunter, damit der Spieler waehrend des
 * Reconnects keine Moves ausloest, die der Server eh ignoriert.
 */
@Composable
fun ReconnectingOverlay(
    state: ConnectionState,
    onBackToMenu: () -> Unit,
    onRetry: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
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
            when (state) {
                ConnectionState.Reconnecting -> ReconnectingBody()
                ConnectionState.LostPermanently -> LostPermanentlyBody(onRetry, onBackToMenu)
                ConnectionState.Connected -> Unit
            }
        }
    }
}

@Composable
private fun ReconnectingBody() {
    Text(
        text = stringResource(R.string.reconnect_overlay_title),
        style = TextStyle(
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = InkBlack,
            textAlign = TextAlign.Center
        )
    )
    Spacer(Modifier.height(8.dp))
    Text(
        text = stringResource(R.string.reconnect_overlay_subtitle),
        style = TextStyle(
            fontSize = 14.sp,
            color = InkBrown,
            textAlign = TextAlign.Center
        )
    )
    Spacer(Modifier.height(24.dp))
    CircularProgressIndicator(color = GoldCoinDark)
}

@Composable
private fun LostPermanentlyBody(onRetry: () -> Unit, onBackToMenu: () -> Unit) {
    Text(
        text = stringResource(R.string.reconnect_lost_title),
        style = TextStyle(
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF8B1A1A),
            textAlign = TextAlign.Center
        )
    )
    Spacer(Modifier.height(8.dp))
    Text(
        text = stringResource(R.string.reconnect_lost_subtitle),
        style = TextStyle(
            fontSize = 14.sp,
            color = InkBrown,
            textAlign = TextAlign.Center
        )
    )
    Spacer(Modifier.height(24.dp))
    Button(
        onClick = onRetry,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = GoldCoinDark,
            contentColor = ParchmentLight
        )
    ) {
        Text(
            text = stringResource(R.string.reconnect_retry),
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = GoldCoinLight,
                letterSpacing = 1.sp
            )
        )
    }
    Spacer(Modifier.height(12.dp))
    Button(
        onClick = onBackToMenu,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = GoldCoinDark,
            contentColor = ParchmentLight
        )
    ) {
        Text(
            text = stringResource(R.string.reconnect_back_to_menu),
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = GoldCoinLight,
                letterSpacing = 1.sp
            )
        )
    }
}
