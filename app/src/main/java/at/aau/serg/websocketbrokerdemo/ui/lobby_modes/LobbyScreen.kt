package at.aau.serg.websocketbrokerdemo.ui.lobby_modes

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import at.aau.serg.websocketbrokerdemo.ui.mainmenu.DialogButton
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

@Composable
fun LobbyScreen(
    mode: GameMode,
    navController: NavController
) {
    var showJoinDialog by remember { mutableStateOf(false) }

    /** Wartelobby-Route je nach Modus. */
    fun toWaitingRoute(): String = when (mode) {
        GameMode.DUAL_VALLEY -> "waiting_dual"
        GameMode.TRIAD_OUTPOST -> "waiting_triad"
        GameMode.BATTLEFIELD_PEAKS -> "waiting_battlefield"
    }

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

        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .roundCoinButton()
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
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
                onClick = { navController.navigate(toWaitingRoute()) }
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
                onClick = { navController.navigate(toWaitingRoute()) }
            )
        }
    }

    if (showJoinDialog) {
        JoinByCodeDialog(
            onDismiss = { showJoinDialog = false },
            onJoin = {
                showJoinDialog = false
                navController.navigate(toWaitingRoute())
            }
        )
    }
}

private enum class SealColor(val main: Color, val dark: Color) {
    Red(Color(0xFF8B1A1A), Color(0xFF5A0F0F)),
    Blue(Color(0xFF1F3A6B), Color(0xFF13264A)),
    Gold(Color(0xFFD4A24C), Color(0xFF9C6F22))
}

@Composable
private fun ActionCard(
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

@Composable
private fun PlayerCountBadge(count: Int, modifier: Modifier = Modifier) {
    val roman = when (count) {
        2 -> "II"
        3 -> "III"
        4 -> "IV"
        else -> count.toString()
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(48.dp)
            .shadow(6.dp, CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(GoldCoinLight, GoldCoin, GoldCoinDark),
                    radius = 70f
                ),
                shape = CircleShape
            )
            .border(2.dp, GoldCoinDark, CircleShape)
    ) {
        Text(
            text = roman,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = InkBlack,
                letterSpacing = 1.sp
            )
        )
    }
}

@Composable
private fun JoinByCodeDialog(
    onDismiss: () -> Unit,
    onJoin: (String) -> Unit
) {
    var code by remember { mutableStateOf("") }
    val canJoin = code.length in 4..8

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .shadow(20.dp, RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.verticalGradient(listOf(ParchmentLight, ParchmentDark)),
                    shape = RoundedCornerShape(16.dp)
                )
                .border(3.dp, GoldCoinDark, RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.dialog_enter_code),
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = InkBlack,
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.dialog_enter_code_sub),
                    style = TextStyle(
                        fontSize = 13.sp,
                        color = InkBrown,
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = code,
                    onValueChange = { input ->
                        code = input.filter { it.isLetterOrDigit() }
                            .take(8)
                            .uppercase()
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters
                    ),
                    textStyle = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = InkBlack,
                        letterSpacing = 6.sp,
                        textAlign = TextAlign.Center
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldCoinDark,
                        unfocusedBorderColor = WoodMedium,
                        focusedContainerColor = ParchmentLight,
                        unfocusedContainerColor = ParchmentLight,
                        cursorColor = InkBlack
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(20.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DialogButton(
                        text = stringResource(R.string.dialog_cancel),
                        primary = false,
                        onClick = onDismiss
                    )
                    DialogButton(
                        text = stringResource(R.string.dialog_join),
                        primary = canJoin,
                        onClick = { if (canJoin) onJoin(code) }
                    )
                }
            }
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
