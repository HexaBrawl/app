package at.aau.serg.websocketbrokerdemo.ui.waiting.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoin
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinDark
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBlack
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentLight
import at.aau.serg.websocketbrokerdemo.ui.theme.WoodMedium
import com.example.myapplication.R

/**
 * Grosser "Bereit"-Button am Ende der lokalen Spielerkarte.
 *
 * Wechselt das Aussehen abhaengig vom ready-Zustand (Gold-Look wenn
 * bereit, Holz-Look wenn nicht). Bei `enabled = false` (z.B. wenn
 * der Name leer ist) wird der Button gedimmt und nicht klickbar.
 */
@Composable
fun ReadyButton(
    ready: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (ready) GoldCoin else WoodMedium,
            contentColor = if (ready) InkBlack else ParchmentLight,
            disabledContainerColor = WoodMedium.copy(alpha = 0.5f),
            disabledContentColor = ParchmentLight.copy(alpha = 0.5f)
        ),
        modifier = modifier
            .height(54.dp)
            .border(2.dp, GoldCoinDark, RoundedCornerShape(10.dp))
    ) {
        Icon(
            imageVector = if (ready) Icons.Filled.Check else Icons.Filled.HourglassEmpty,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = if (ready) stringResource(R.string.waiting_ready)
            else stringResource(R.string.waiting_not_ready),
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )
        )
    }
}
