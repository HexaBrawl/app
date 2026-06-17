package at.aau.serg.websocketbrokerdemo.ui.game.tophud.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentDark
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentLight
import com.example.myapplication.R

/**
 * Menue-Popup ueber dem Top-HUD.
 *
 * Bietet drei Aktionen: zu den Einstellungen wechseln, das Info-Popup
 * mit der Spielerklaerung oeffnen, oder den aktuellen Spielstand neu vom
 * Server anfordern ([onSync]) -- nuetzlich, wenn der Client durch einen
 * verpassten Broadcast haengt (z. B. ein Mitspieler ist weg, wird aber
 * noch angezeigt). Stateless -- alle Callbacks kommen von aussen.
 */
@Composable
fun HudMenuPopup(
    onSettings: () -> Unit,
    onInfo: () -> Unit,
    onSync: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
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
                    text = stringResource(R.string.hud_menu_title),
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = InkBlack
                    )
                )

                Spacer(Modifier.height(20.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DialogButton(
                        text = stringResource(R.string.hud_menu_settings),
                        primary = true,
                        onClick = onSettings
                    )
                }

                Spacer(Modifier.height(10.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DialogButton(
                        text = stringResource(R.string.hud_menu_info),
                        primary = false,
                        onClick = onInfo
                    )
                }

                Spacer(Modifier.height(10.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DialogButton(
                        text = stringResource(R.string.hud_menu_sync),
                        primary = false,
                        onClick = onSync
                    )
                }
            }
        }
    }
}