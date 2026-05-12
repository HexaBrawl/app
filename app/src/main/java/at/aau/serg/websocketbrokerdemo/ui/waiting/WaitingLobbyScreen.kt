package at.aau.serg.websocketbrokerdemo.ui.waiting

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import at.aau.serg.websocketbrokerdemo.ui.components.PlayerCountBadge
import at.aau.serg.websocketbrokerdemo.ui.mainmenu.GameMode
import at.aau.serg.websocketbrokerdemo.ui.theme.*
import com.example.myapplication.R

@Composable
fun WaitingLobbyScreen(
    mode: GameMode,
    navController: NavController
) {

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        MusicManager.playTournamentMusic(context)
    }

    // --- STATE ---
    val slots = rememberLobbySlots(mode)
    val allReady = rememberAllReady(slots)
    val countdown = rememberCountdown(allReady, navController)

    AutoFillLobbySlots(slots)

    // --- UI ---
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
                .padding(
                    top = 80.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 24.dp
                )
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

                val takenColors = slots
                    .filter {
                        it.id != slot.id &&
                                it.status != SlotStatus.Empty
                    }
                    .map { it.color }
                    .toSet()

                when {

                    slot.status == SlotStatus.Empty -> {
                        EmptySlotCard()
                    }

                    slot.isLocal -> {
                        LocalPlayerSlotCard(
                            slot = slot,
                            takenColors = takenColors,

                            onNameChange = { newName ->
                                slots[index] = slot.copy(name = newName)
                            },

                            onColorChange = { newColor ->
                                slots[index] = slot.copy(color = newColor)
                            },

                            onReadyToggle = {
                                slots[index] =
                                    slot.copy(ready = !slot.ready)
                            }
                        )
                    }

                    else -> {
                        RemotePlayerSlotCard(slot)
                    }
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
    .background(
        brush = Brush.radialGradient(
            listOf(WoodLight, WoodDark)
        ),
        shape = CircleShape
    )
    .border(2.dp, GoldCoinDark, CircleShape)