package at.aau.serg.websocketbrokerdemo.ui.mainmenu.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBlack
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBrown

/**
 * Eine Zeile im InfoDialog: fetter Label oben, normaler Body darunter.
 * Wird für jede Spielregel-Kategorie (Kampf/Wirtschaft/etc.) wiederverwendet.
 */
@Composable
fun InfoDialogBody(label: String, body: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = InkBlack,
                letterSpacing = 1.sp
            )
        )
        Text(
            text = body,
            style = TextStyle(fontSize = 13.sp, color = InkBrown)
        )
    }
}
