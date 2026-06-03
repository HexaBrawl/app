package at.aau.serg.websocketbrokerdemo.ui.lobby_modes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import at.aau.serg.websocketbrokerdemo.ui.components.PlayerCountBadge
import at.aau.serg.websocketbrokerdemo.ui.components.RoundCoinIconButton
import at.aau.serg.websocketbrokerdemo.ui.mainmenu.GameMode
import at.aau.serg.websocketbrokerdemo.ui.theme.WoodDark
import at.aau.serg.websocketbrokerdemo.ui.theme.WoodMedium
import com.example.myapplication.R

/**
 * Modus-Lobby: Auswahl zwischen "Privates Spiel erstellen", "Mit Code
 * beitreten" und "Zufaelliges Spiel".
 *
 * Reine UI-Schicht. State und Dialog-Handler liegen im
 * [LobbyViewModel]; Navigation-Mapping in [LobbyLogic].
 */
@Composable
fun LobbyScreen(
    mode: GameMode,
    navController: NavController,
    viewModel: LobbyViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val waitingScreen = LobbyLogic.toWaitingScreen(mode)

    Box(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(WoodMedium, WoodDark),
                        radius = 1500f
                    )
                )
        )

        Image(
            painter = painterResource(id = mode.backgroundRes),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.35f)
                        )
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
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 240.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ActionCard(
                icon = Icons.Filled.Lock,
                title = stringResource(R.string.lobby_create_private),
                subtitle = stringResource(R.string.lobby_create_private_sub),
                sealColor = SealColor.Red,
                onClick = { navController.navigate(waitingScreen.route) }
            )
            ActionCard(
                icon = Icons.Filled.GroupAdd,
                title = stringResource(R.string.lobby_join_with_code),
                subtitle = stringResource(R.string.lobby_join_with_code_sub),
                sealColor = SealColor.Blue,
                onClick = { viewModel.openJoinDialog() }
            )
            ActionCard(
                icon = Icons.Filled.Casino,
                title = stringResource(R.string.lobby_join_random),
                subtitle = stringResource(R.string.lobby_join_random_sub),
                sealColor = SealColor.Gold,
                onClick = { navController.navigate(waitingScreen.route) }
            )
        }
    }

    if (state.showJoinDialog) {
        JoinByCodeDialog(
            code = state.code,
            canJoin = state.canJoin,
            onCodeChange = viewModel::onCodeChange,
            onDismiss = viewModel::closeJoinDialog,
            onJoin = {
                if (viewModel.tryJoinByCode()) {
                    navController.navigate(waitingScreen.route)
                }
            }
        )
    }
}
