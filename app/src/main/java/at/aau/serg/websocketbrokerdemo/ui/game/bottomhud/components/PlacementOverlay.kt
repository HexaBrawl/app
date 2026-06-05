package at.aau.serg.websocketbrokerdemo.ui.game.bottomhud.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
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
import androidx.compose.foundation.clickable
import at.aau.serg.websocketbrokerdemo.data.serverside.UnitType
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinDark
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBlack
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentDark
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentLight
import com.example.myapplication.R

/**
 * Hinweis-Banner im Platzierungs-Modus.
 *
 * Sitzt oben mittig auf dem Bildschirm und zeigt dem Spieler an, welche
 * Einheit er gerade platziert. Mit einem X-Button kann er den Modus
 * abbrechen.
 *
 * Blockiert NICHT die Karte -- der Spieler soll ja auf eine Zelle
 * tippen koennen.
 */
@Composable
fun PlacementOverlay(
    type: UnitType,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val typeLabel = when (type) {
        UnitType.INFANTRY -> stringResource(R.string.unit_infantry)
        UnitType.ARCHER -> stringResource(R.string.unit_archer)
        UnitType.CAVALRY -> stringResource(R.string.unit_cavalry)
        UnitType.SKELETON -> ""
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Spacer(Modifier.weight(1f))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .shadow(8.dp, RoundedCornerShape(12.dp))
                .background(
                    brush = Brush.verticalGradient(listOf(ParchmentLight, ParchmentDark)),
                    shape = RoundedCornerShape(12.dp)
                )
                .border(2.dp, GoldCoinDark, RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.placement_choose_cell, typeLabel),
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = InkBlack
                )
            )

            Spacer(Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .clickable(onClick = onCancel)
                    .padding(2.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                    tint = InkBlack
                )
            }
        }

        Spacer(Modifier.weight(1f))
    }
}
