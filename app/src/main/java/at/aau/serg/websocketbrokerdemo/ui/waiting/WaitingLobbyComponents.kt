package at.aau.serg.websocketbrokerdemo.ui.waiting

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import at.aau.serg.websocketbrokerdemo.ui.theme.*
import com.example.myapplication.R

// --- Slot-Karten ---

@Composable
fun LocalPlayerSlotCard(
    slot: PlayerSlot,
    takenColors: Set<PlayerColor>,
    onNameChange: (String) -> Unit,
    onColorChange: (PlayerColor) -> Unit,
    onReadyToggle: () -> Unit
) {
    val canBeReady = slot.name.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(14.dp))
            .background(
                brush = Brush.verticalGradient(listOf(ParchmentLight, ParchmentDark)),
                shape = RoundedCornerShape(14.dp)
            )
            .border(3.dp, GoldCoinDark, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        brush = Brush.radialGradient(listOf(slot.color.main, slot.color.dark)),
                        shape = CircleShape
                    )
                    .border(2.dp, GoldCoinDark, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = ParchmentLight,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.waiting_your_orders),
                style = TextStyle(
                    fontSize = 13.sp,
                    color = InkBrown,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                )
            )
        }

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = slot.name,
            onValueChange = onNameChange,
            singleLine = true,
            enabled = !slot.ready,
            keyboardOptions = KeyboardOptions.Default,
            label = {
                Text(stringResource(R.string.waiting_your_name), style = TextStyle(fontSize = 12.sp))
            },
            leadingIcon = {
                Icon(Icons.Filled.Edit, contentDescription = null, tint = InkBrown)
            },
            textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = InkBlack),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GoldCoinDark,
                unfocusedBorderColor = WoodMedium,
                disabledBorderColor = WoodMedium,
                focusedContainerColor = ParchmentLight,
                unfocusedContainerColor = ParchmentLight,
                disabledContainerColor = ParchmentLight,
                focusedLabelColor = InkBrown,
                unfocusedLabelColor = InkBrown,
                disabledTextColor = InkBlack,
                cursorColor = InkBlack
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(14.dp))

        Text(
            text = stringResource(R.string.waiting_choose_color),
            style = TextStyle(
                fontSize = 12.sp,
                color = InkBrown,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        )
        Spacer(Modifier.height(6.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            PlayerColor.entries.forEach { color ->
                ColorSeal(
                    color = color,
                    selected = slot.color == color,
                    disabled = color in takenColors || slot.ready,
                    onClick = { onColorChange(color) }
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        ReadyButton(
            ready = slot.ready,
            enabled = canBeReady,
            onClick = onReadyToggle,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Composable
fun RemotePlayerSlotCard(slot: PlayerSlot) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(10.dp))
            .background(
                brush = Brush.verticalGradient(listOf(ParchmentLight, ParchmentDark)),
                shape = RoundedCornerShape(10.dp)
            )
            .border(2.dp, GoldCoinDark, RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .background(
                    brush = Brush.radialGradient(listOf(slot.color.main, slot.color.dark)),
                    shape = CircleShape
                )
                .border(2.dp, GoldCoinDark, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                tint = ParchmentLight,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = slot.name,
                style = TextStyle(
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = InkBlack
                )
            )
            Text(
                text = if (slot.status == SlotStatus.Bot)
                    stringResource(R.string.waiting_bot_ally)
                else
                    stringResource(R.string.waiting_remote_ally),
                style = TextStyle(fontSize = 12.sp, color = InkBrown)
            )
        }
        ReadyBadge(ready = slot.ready)
    }
}

@Composable
fun EmptySlotCard() {
    val infinite = rememberInfiniteTransition(label = "emptySlotPulse")
    val pulseAlpha by infinite.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
        label = "pulseAlpha"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(10.dp))
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        ParchmentLight.copy(alpha = 0.40f),
                        ParchmentDark.copy(alpha = 0.40f)
                    )
                ),
                shape = RoundedCornerShape(10.dp)
            )
            .border(
                1.5.dp,
                GoldCoinDark.copy(alpha = pulseAlpha * 0.6f),
                RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .background(
                    brush = Brush.radialGradient(
                        listOf(
                            WoodLight.copy(alpha = 0.6f),
                            WoodDark.copy(alpha = 0.6f)
                        )
                    ),
                    shape = CircleShape
                )
                .border(1.5.dp, GoldCoinDark.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Filled.PersonOutline,
                contentDescription = null,
                tint = GoldCoinLight.copy(alpha = pulseAlpha),
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = stringResource(R.string.waiting_empty_slot),
            style = TextStyle(
                fontSize = 14.sp,
                color = InkBrown.copy(alpha = pulseAlpha),
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Italic
            )
        )
    }
}



// --- Kleine Komponenten ---

@Composable
fun ColorSeal(
    color: PlayerColor,
    selected: Boolean,
    disabled: Boolean,
    onClick: () -> Unit
) {
    val borderWidth = if (selected) 3.dp else 1.5.dp
    val borderColor = if (selected) GoldCoinLight else GoldCoinDark

    val baseModifier = Modifier
        .size(46.dp)
        .shadow(if (selected) 6.dp else 2.dp, CircleShape)
        .background(
            brush = Brush.radialGradient(
                colors = if (disabled && !selected) {
                    listOf(
                        color.main.copy(alpha = 0.35f),
                        color.dark.copy(alpha = 0.35f)
                    )
                } else {
                    listOf(color.main, color.dark)
                }
            ),
            shape = CircleShape
        )
        .border(borderWidth, borderColor, CircleShape)

    val finalModifier = if (disabled) baseModifier else baseModifier.clickable(onClick = onClick)

    Box(
        contentAlignment = Alignment.Center,
        modifier = finalModifier
    ) {
        if (selected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = ParchmentLight,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

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

@Composable
fun ReadyBadge(ready: Boolean) {
    val main = if (ready) Color(0xFF2D5A1A) else Color(0xFF6B6B6B)
    val dark = if (ready) Color(0xFF1A3A0F) else Color(0xFF3A3A3A)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(40.dp)
            .shadow(3.dp, CircleShape)
            .background(brush = Brush.radialGradient(listOf(main, dark)), shape = CircleShape)
            .border(2.dp, GoldCoinDark, CircleShape)
    ) {
        Icon(
            imageVector = if (ready) Icons.Filled.Check else Icons.Filled.HourglassEmpty,
            contentDescription = null,
            tint = ParchmentLight,
            modifier = Modifier.size(20.dp)
        )
    }
}


@Composable
fun CountdownOverlay(seconds: Int) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .wrapContentSize()
            .shadow(20.dp, CircleShape)
            .background(
                brush = Brush.radialGradient(listOf(GoldCoinLight, GoldCoin, GoldCoinDark)),
                shape = CircleShape
            )
            .border(4.dp, GoldCoinDark, CircleShape)
            .padding(40.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Filled.HourglassBottom,
                contentDescription = null,
                tint = InkBlack,
                modifier = Modifier.size(40.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = seconds.toString(),
                style = TextStyle(
                    fontSize = 60.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = InkBlack
                )
            )
        }
    }
}
