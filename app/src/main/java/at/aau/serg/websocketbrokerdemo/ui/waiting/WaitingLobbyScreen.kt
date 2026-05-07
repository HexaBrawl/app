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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import at.aau.serg.websocketbrokerdemo.audio.MusicManager
import at.aau.serg.websocketbrokerdemo.ui.mainmenu.GameMode
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinDark
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinLight
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentLight
import at.aau.serg.websocketbrokerdemo.ui.theme.WoodDark
import at.aau.serg.websocketbrokerdemo.ui.theme.WoodLight
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

private fun Modifier.roundCoinButton(): Modifier = this
    .size(48.dp)
    .shadow(6.dp, CircleShape)
    .background(
        brush = Brush.radialGradient(listOf(WoodLight, WoodDark)),
        shape = CircleShape
    )
    .border(2.dp, GoldCoinDark, CircleShape)
