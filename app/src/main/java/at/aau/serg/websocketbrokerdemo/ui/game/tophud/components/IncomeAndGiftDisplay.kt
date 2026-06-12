package at.aau.serg.websocketbrokerdemo.ui.game.tophud.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinDark
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBlack
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentDark
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentLight
import com.example.myapplication.R

/**
 * Mittlere HUD-Box: Einkommen pro Runde + Geschenk-Button.
 *
 *  - [income]         Goldeinkommen pro Runde.
 *  - [giftEnabled]    false = Spieler hat sein Geschenk bereits benutzt
 *                     (Server-Truth via Player.hasUsedGift); das Icon
 *                     wird dann grayed out und nicht mehr klickbar.
 *  - [onIncomeClick]  Wird ausgeloest, wenn der Spieler auf die
 *                     "+X /Runde" Box tippt. Oeffnet das Income-Detail-
 *                     Popup mit der Aufschluesselung.
 *  - [onGiftClick]    Wird beim 5. Klick zum Auswerfen verwendet.
 */
@Composable
fun IncomeAndGiftDisplay(
    income: Int,
    giftEnabled: Boolean,
    onIncomeClick: () -> Unit,
    onGiftClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(10.dp))
            .background(
                brush = Brush.verticalGradient(listOf(ParchmentLight, ParchmentDark)),
                shape = RoundedCornerShape(10.dp)
            )
            .border(2.dp, GoldCoinDark, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(onClick = onIncomeClick)
        ) {
            Text(
                text = if (income >= 0) "+$income" else "$income",
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (income >= 0) Color(0xFF2D7A1F) else Color(0xFFB22222)
                )
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.hud_per_round),
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = InkBlack
                )
            )
        }
        Spacer(Modifier.width(10.dp))

        val giftModifier = Modifier
            .size(40.dp)
            .alpha(if (giftEnabled) 1f else 0.35f)
            .let { if (giftEnabled) it.clickable(onClick = onGiftClick) else it }

        Image(
            painter = painterResource(id = R.drawable.random_surprise),
            contentDescription = null,
            modifier = giftModifier
        )
    }
}
