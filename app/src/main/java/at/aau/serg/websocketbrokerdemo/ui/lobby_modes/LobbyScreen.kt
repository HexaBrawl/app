package at.aau.serg.websocketbrokerdemo.ui.lobby_modes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import at.aau.serg.websocketbrokerdemo.ui.components.PlayerCountBadge
import at.aau.serg.websocketbrokerdemo.ui.mainmenu.GameMode
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinLight
import at.aau.serg.websocketbrokerdemo.ui.theme.WoodDark
import at.aau.serg.websocketbrokerdemo.ui.theme.WoodMedium
import com.example.myapplication.R

@Composable
fun LobbyScreen(
    mode: GameMode,
    navController: NavController
) {
    var showJoinDialog by remember { mutableStateOf(false) }

    val waitingRoute = LobbyLogic.toWaitingRoute(mode)

    Box(modifier = Modifier.fillMaxSize()) {

        // Hintergrund
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

        // Hintergrundbild
        Image(
            painter = painterResource(id = mode.backgroundRes),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        // Vignette
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

        // Back Button
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

        // Player Count Badge
        PlayerCountBadge(
            count = mode.playerCount,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        )

        // Action Cards
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
                onClick = { navController.navigate(waitingRoute) }
            )
            ActionCard(
                icon = Icons.Filled.GroupAdd,
                title = stringResource(R.string.lobby_join_with_code),
                subtitle = stringResource(R.string.lobby_join_with_code_sub),
                sealColor = SealColor.Blue,
                onClick = { showJoinDialog = true }
            )
            ActionCard(
                icon = Icons.Filled.Casino,
                title = stringResource(R.string.lobby_join_random),
                subtitle = stringResource(R.string.lobby_join_random_sub),
                sealColor = SealColor.Gold,
                onClick = { navController.navigate(waitingRoute) }
            )
        }
    }

    if (showJoinDialog) {
        JoinByCodeDialog(
            onDismiss = { showJoinDialog = false },
            onJoin = {
                showJoinDialog = false
                navController.navigate(waitingRoute)
            }
        )
    }
}
