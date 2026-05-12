package at.aau.serg.websocketbrokerdemo.ui.lobby_modes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.aau.serg.websocketbrokerdemo.ui.theme.*

enum class SealColor(val main: Color, val dark: Color) {
    Red(Color(0xFF8B1A1A), Color(0xFF5A0F0F)),
    Blue(Color(0xFF1F3A6B), Color(0xFF13264A)),
    Gold(Color(0xFFD4A24C), Color(0xFF9C6F22))
}

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
                .size(54.dp)
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
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = InkBlack,
                    letterSpacing = 0.5.sp
                )
            )
            Text(
                text = subtitle,
                style = TextStyle(fontSize = 12.sp, color = InkBrown)
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
