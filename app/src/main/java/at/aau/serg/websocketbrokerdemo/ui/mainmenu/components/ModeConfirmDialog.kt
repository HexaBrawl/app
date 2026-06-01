package at.aau.serg.websocketbrokerdemo.ui.mainmenu.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import at.aau.serg.websocketbrokerdemo.ui.mainmenu.GameMode
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinDark
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBlack
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBrown
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentDark
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentLight
import com.example.myapplication.R

/**
 * Wachssiegel-Bestätigung im Pergament-Look.
 * Wird nach dem Tap auf einen Hotspot angezeigt -- der Spieler muss
 * den Modus bestätigen bevor wir in die Lobby navigieren.
 */
@Composable
fun ModeConfirmDialog(
    mode: GameMode,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .shadow(20.dp, RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.verticalGradient(listOf(ParchmentLight, ParchmentDark)),
                    shape = RoundedCornerShape(16.dp)
                )
                .border(3.dp, GoldCoinDark, RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.menu_choose_battlefield),
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = InkBrown,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 2.sp,
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(mode.nameRes),
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = InkBlack,
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(mode.taglineRes),
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = InkBrown,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.menu_player_count, mode.playerCount),
                    style = TextStyle(fontSize = 13.sp, color = InkBrown, textAlign = TextAlign.Center)
                )
                Spacer(Modifier.height(20.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DialogButton(
                        text = stringResource(R.string.dialog_cancel),
                        primary = false,
                        onClick = onDismiss
                    )
                    DialogButton(
                        text = stringResource(R.string.dialog_march),
                        primary = true,
                        onClick = onConfirm
                    )
                }
            }
        }
    }
}
