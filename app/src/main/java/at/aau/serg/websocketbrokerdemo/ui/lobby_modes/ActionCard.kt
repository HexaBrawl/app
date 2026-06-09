package at.aau.serg.websocketbrokerdemo.ui.lobby_modes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinDark
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBlack
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBrown
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentDark
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentLight

/**
 * Aktionskarte im Pergament-Look mit Wachs-Siegel.
 *
 * Wird in der Modus-Lobby als grosse, antippbare Karte fuer die drei
 * Lobby-Aktionen verwendet (Privates Spiel, mit Code beitreten,
 * zufaelliges Spiel). Das Wachs-Siegel links zeigt das Icon der Aktion
 * in einer fuer die Aktion charakteristischen Farbe (siehe [SealColor]).
 */
@Composable
fun ActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    sealColor: SealColor,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .background(
                brush = Brush.verticalGradient(listOf(ParchmentLight, ParchmentDark)),
                shape = RoundedCornerShape(12.dp)
            )
            .border(2.dp, GoldCoinDark, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(84.dp)
                .shadow(4.dp, CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(sealColor.main, sealColor.dark),
                        radius = 80f
                    ),
                    shape = CircleShape
                )
                .border(2.dp, GoldCoinDark, CircleShape)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ParchmentLight,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = InkBlack,
                    letterSpacing = 0.5.sp
                )
            )
            Text(
                text = subtitle,
                style = TextStyle(fontSize = 14.sp, color = InkBrown)
            )
        }

        Text(
            text = "›",
            style = TextStyle(
                fontSize = 28.sp,
                color = GoldCoinDark,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(end = 8.dp)
        )
    }
}
