package at.aau.serg.websocketbrokerdemo.ui.waiting

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import at.aau.serg.websocketbrokerdemo.network.GameSession
import at.aau.serg.websocketbrokerdemo.ui.components.PlayerCountBadge
import at.aau.serg.websocketbrokerdemo.ui.components.RoundCoinIconButton
import at.aau.serg.websocketbrokerdemo.ui.mainmenu.GameMode
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinLight
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentLight
import at.aau.serg.websocketbrokerdemo.ui.theme.WoodDark
import at.aau.serg.websocketbrokerdemo.ui.waiting.components.CountdownOverlay
import at.aau.serg.websocketbrokerdemo.ui.waiting.components.EmptySlotCard
import at.aau.serg.websocketbrokerdemo.ui.waiting.components.LocalPlayerSlotCard
import at.aau.serg.websocketbrokerdemo.ui.waiting.components.RemotePlayerSlotCard
import at.aau.serg.websocketbrokerdemo.ui.waiting.model.SlotStatus
import com.example.myapplication.R

/**
 * Wartelobby -- "Versammlung der Generaele".
 *
 * Reine UI-Schicht. Die Slot-Verwaltung, Countdown-Logik und remote-
 * State-Anwendung liegt im [WaitingLobbyViewModel]; Netzwerk-Side-
 * Effects (Server-Subscription, Spielstart-Navigation) in
 * [LobbyNetworkSync].
 * Zeigt die Spieler-Slots, ermoeglicht Namens-/Farbwahl und startet
 * einen 3-Sekunden-Countdown, sobald alle Slots besetzt und bereit
 * sind.
 *
 * Hinweis: Diese Datei wird im Network-Refactor-Zuge umgebaut (echte
 * Server-Slots statt lokalem State, ggf. ViewModel-Anbindung). Hier
 * wurden nur Navigations- und Button-Aufrufe an das type-safe
 * Screen-Konstrukt angepasst.
 */
@Composable
fun WaitingLobbyScreen(
    mode: GameMode,
    navController: NavController,
    session: GameSession,
    viewModel: WaitingLobbyViewModel = viewModel(
        factory = viewModelFactory {
            initializer { WaitingLobbyViewModel(mode) }
        }
    )
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LobbyNetworkSync(
        session = session,
        viewModel = viewModel,
        navController = navController
    )

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

        RoundCoinIconButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.settings_back),
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        )

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

            state.slots.forEach { slot ->
                val takenColors = WaitingLobbyLogic.takenColorsExcept(state.slots, slot.id)
                when {
                    slot.status == SlotStatus.Empty -> EmptySlotCard()
                    slot.isLocal -> LocalPlayerSlotCard(
                        slot = slot,
                        takenColors = takenColors,
                        onNameChange = { viewModel.onNameChange(slot.id, it) },
                        onColorChange = { viewModel.onColorChange(slot.id, it) },
                        onReadyToggle = { viewModel.onReadyToggle(slot.id) }
                    )
                    else -> RemotePlayerSlotCard(slot)
                }
            }
        }

        AnimatedVisibility(
            visible = state.isCountdownActive,
            modifier = Modifier.align(Alignment.Center)
        ) {
            CountdownOverlay(seconds = state.countdown)
        }
    }
}
