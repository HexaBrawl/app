package at.aau.serg.websocketbrokerdemo.ui.mainmenu.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinDark
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBlack
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentDark
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentLight
import com.example.myapplication.R

/**
 * Pergament-Dialog mit den Spielregeln.
 * Geöffnet über den Info-Button (oben rechts im Hauptmenü).
 */
@Composable
fun InfoDialog(onDismiss: () -> Unit) {
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
                    text = stringResource(R.string.info_title),
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = InkBlack
                    )
                )
                Spacer(Modifier.height(12.dp))
                InfoRow(
                    label = stringResource(R.string.info_combat_label),
                    body = stringResource(R.string.info_combat)
                )
                InfoRow(
                    label = stringResource(R.string.info_economy_label),
                    body = stringResource(R.string.info_economy)
                )
                InfoRow(
                    label = stringResource(R.string.info_supply_label),
                    body = stringResource(R.string.info_supply)
                )
                InfoRow(
                    label = stringResource(R.string.info_terrain_label),
                    body = stringResource(R.string.info_terrain)
                )
                Spacer(Modifier.height(16.dp))
                DialogButton(
                    text = stringResource(R.string.dialog_understood),
                    primary = true,
                    onClick = onDismiss
                )
            }
        }
    }
}
