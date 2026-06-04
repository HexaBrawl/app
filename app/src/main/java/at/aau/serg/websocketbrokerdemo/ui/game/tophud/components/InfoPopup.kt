package at.aau.serg.websocketbrokerdemo.ui.game.tophud.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
 * Info-Popup mit der Spielerklaerung.
 *
 * Enthaelt am Ende einen rot eingefaerbten Schummel-Hinweis, der
 * Spieler auf die Geschenk-Mechanik aufmerksam macht (zufaelliger
 * Gold-Gewinn / -Verlust bei mehrfachem Antippen).
 */
@Composable
fun InfoPopup(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .shadow(20.dp, RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.verticalGradient(listOf(ParchmentLight, ParchmentDark)),
                    shape = RoundedCornerShape(16.dp)
                )
                .border(3.dp, GoldCoinDark, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.hud_info_title),
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = InkBlack
                )
            )

            Spacer(Modifier.height(14.dp))

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.hud_info_body),
                    style = TextStyle(fontSize = 14.sp, color = InkBlack)
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.hud_info_cheat_title),
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = InkBrown
                    )
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.hud_info_cheat_body),
                    style = TextStyle(fontSize = 14.sp, color = InkBlack)
                )
            }

            Spacer(Modifier.height(16.dp))

            DialogButton(
                text = stringResource(R.string.dialog_close),
                primary = true,
                onClick = onDismiss
            )
        }
    }
}
