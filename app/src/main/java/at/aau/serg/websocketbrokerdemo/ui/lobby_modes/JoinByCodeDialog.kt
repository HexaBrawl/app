package at.aau.serg.websocketbrokerdemo.ui.lobby_modes

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import at.aau.serg.websocketbrokerdemo.ui.mainmenu.components.DialogButton
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinDark
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBlack
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBrown
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentDark
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentLight
import at.aau.serg.websocketbrokerdemo.ui.theme.WoodMedium
import com.example.myapplication.R

/**
 * Pergament-Dialog zum Beitreten einer Lobby per Code.
 *
 * Stateless: code, canJoin und alle Handler kommen von aussen (aus dem
 * [LobbyViewModel]). Damit haelt der Dialog selbst nichts und ist
 * komplett von der UI-Schicht abgekoppelt.
 */
@Composable
fun JoinByCodeDialog(
    code: String,
    canJoin: Boolean,
    onCodeChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onJoin: () -> Unit
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
                    text = stringResource(R.string.dialog_enter_code),
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = InkBlack
                    )
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.dialog_enter_code_sub),
                    style = TextStyle(fontSize = 13.sp, color = InkBrown)
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = code,
                    onValueChange = onCodeChange,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters
                    ),
                    textStyle = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = InkBlack,
                        letterSpacing = 6.sp
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldCoinDark,
                        unfocusedBorderColor = WoodMedium,
                        focusedContainerColor = ParchmentLight,
                        unfocusedContainerColor = ParchmentLight,
                        cursorColor = InkBlack
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(20.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DialogButton(
                        text = stringResource(R.string.dialog_cancel),
                        primary = false,
                        onClick = onDismiss
                    )
                    DialogButton(
                        text = stringResource(R.string.dialog_join),
                        primary = canJoin,
                        onClick = onJoin
                    )
                }
            }
        }
    }
}
