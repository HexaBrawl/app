package at.aau.serg.websocketbrokerdemo.ui.waiting

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import at.aau.serg.websocketbrokerdemo.audio.MusicManager
import at.aau.serg.websocketbrokerdemo.ui.mainmenu.GameMode
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoin
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinDark
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinLight
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBlack
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBrown
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentDark
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentLight
import at.aau.serg.websocketbrokerdemo.ui.theme.WoodDark
import at.aau.serg.websocketbrokerdemo.ui.theme.WoodLight
import at.aau.serg.websocketbrokerdemo.ui.theme.WoodMedium
import com.example.myapplication.R
import kotlinx.coroutines.delay
import at.aau.serg.websocketbrokerdemo.ui.components.PlayerCountBadge

private const val DEV_AUTO_FILL_SLOTS = true

private val GENERAL_NAMES = listOf(
    "General Aldric", "General Borian", "General Cassia", "General Domitian",
    "General Eolyn", "General Faramond", "General Greta", "General Hadrik",
    "General Isolde", "General Joren", "General Käthe", "General Leofric",
    "General Mira", "General Nikolaus", "General Ortrun",
    "Lord-Marshal Quentin", "Hauptmann Reinhart", "Feldherr Sigmund",
    "Marschall Theodora", "General Ulric"
)

@Composable
fun WaitingLobbyScreen(
    mode: GameMode,
    navController: NavController
) {
    val context = LocalContext.current

    // Turnier-Track starten (wechselt vom Menü-Track sobald wir hier ankommen)
    LaunchedEffect(Unit) {
        MusicManager.playTournamentMusic(context)
    }

    val slots = remember {
        val list = mutableStateListOf<PlayerSlot>()
        list.add(
            PlayerSlot(
                id = 0,
                status = SlotStatus.Player,
                name = GENERAL_NAMES.random(),
                color = PlayerColor.Red,
                ready = false,
                isLocal = true
            )
        )
        for (i in 1 until mode.playerCount) {
            list.add(PlayerSlot(id = i))
        }
        list
    }

    val allReady by remember {
        derivedStateOf {
            slots.all { it.status != SlotStatus.Empty && it.ready }
        }
    }

    var countdown by remember { mutableIntStateOf(-1) }
    LaunchedEffect(allReady) {
        if (allReady) {
            countdown = 3
            while (countdown > 0) {
                delay(1000)
                countdown--
            }
            navController.navigate("game") {
                popUpTo("home") { inclusive = false }
            }
        } else {
            countdown = -1
        }
    }

    if (DEV_AUTO_FILL_SLOTS) {
        LaunchedEffect(Unit) {
            delay(5000)
            slots.forEachIndexed { i, slot ->
                if (slot.status == SlotStatus.Empty) {
                    val taken = slots.filter { it.id != slot.id && it.status != SlotStatus.Empty }
                        .map { it.color }
                        .toSet()
                    val freeColor = PlayerColor.entries.firstOrNull { it !in taken }
                        ?: PlayerColor.Red
                    slots[i] = slot.copy(
                        status = SlotStatus.Bot,
                        name = "Bot-${GENERAL_NAMES.random().substringAfter(' ')}",
                        color = freeColor,
                        ready = true
                    )
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(WoodDark)
        )

        Image(
            painter = painterResource(id = R.drawable.bg_waiting_lobby),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.55f),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = 800f
                    )
                )
        )

        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .roundCoinButton()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.settings_back),
                tint = GoldCoinLight
            )
        }

        PlayerCountBadge(
            count = mode.playerCount,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp, start = 16.dp, end = 16.dp, bottom = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.waiting_headline),
                style = TextStyle(
                    fontSize = 14.sp,
                    color = GoldCoinLight,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 3.sp,
                    textAlign = TextAlign.Center
                )
            )
            Text(
                text = stringResource(mode.nameRes),
                style = TextStyle(
                    fontSize = 26.sp,
                    color = ParchmentLight,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(Modifier.height(4.dp))

            slots.forEachIndexed { index, slot ->
                val takenColors = slots.filter { it.id != slot.id && it.status != SlotStatus.Empty }
                    .map { it.color }
                    .toSet()

                when {
                    slot.status == SlotStatus.Empty -> EmptySlotCard()

                    slot.isLocal -> LocalPlayerSlotCard(
                        slot = slot,
                        takenColors = takenColors,
                        onNameChange = { newName ->
                            slots[index] = slot.copy(name = newName)
                        },
                        onColorChange = { newColor ->
                            slots[index] = slot.copy(color = newColor)
                        },
                        onReadyToggle = {
                            slots[index] = slot.copy(ready = !slot.ready)
                        }
                    )

                    else -> RemotePlayerSlotCard(slot = slot)
                }
            }
        }

        AnimatedVisibility(
            visible = countdown > 0,
            modifier = Modifier.align(Alignment.Center)
        ) {
            CountdownOverlay(seconds = countdown)
        }
    }
}

// --- Slot-Karten ---

@Composable
private fun LocalPlayerSlotCard(
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
private fun RemotePlayerSlotCard(slot: PlayerSlot) {
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
private fun EmptySlotCard() {
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
private fun ColorSeal(
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
private fun ReadyButton(
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
private fun ReadyBadge(ready: Boolean) {
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
private fun CountdownOverlay(seconds: Int) {
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

private fun Modifier.roundCoinButton(): Modifier = this
    .size(48.dp)
    .shadow(6.dp, CircleShape)
    .background(
        brush = Brush.radialGradient(listOf(WoodLight, WoodDark)),
        shape = CircleShape
    )
    .border(2.dp, GoldCoinDark, CircleShape)
