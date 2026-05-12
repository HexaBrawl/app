package at.aau.serg.websocketbrokerdemo.ui.mainmenu

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
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

/**
 * Hauptmenü "Kriegstisch":
 *  - Hintergrundbild zeigt die Übersichtskarte mit drei roten X-Markierungen.
 *  - Über jedem X liegt eine unsichtbare clickable Box (Touch-Target), die
 *    leicht pulsiert um den Spieler zur Auswahl einzuladen.
 *  - Info-Button öffnet einen Pergament-Dialog mit den Spielregeln.
 *  - Nach Tap auf ein X wird ein Wachssiegel-Bestätigungs-Popup gezeigt,
 *    bevor wir in die Lobby navigieren.
 *
 *  Die Hotspot-Positionen sind als Prozentwerte des Bildes definiert,
 *  damit sie auf jedem Bildschirm passen (vorausgesetzt das Bild rendert
 *  mit ContentScale.Fit, was wir hier garantieren).
 */
@Composable
fun MainMenuScreen(navController: NavController) {

    var showInfo by remember { mutableStateOf(false) }
    var pendingMode by remember { mutableStateOf<GameMode?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {

        // Holz-Hintergrund (sichtbar an den Rändern, falls Bild kleiner ist)
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

        // Karte: BoxWithConstraints liefert uns die tatsächlichen Pixel-
        // Maße, anhand derer wir die Hotspots positionieren können.
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            val w = maxWidth
            val h = maxHeight

            Image(
                painter = painterResource(id = R.drawable.bg_mainmenu),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )

            // Hotspots — Positionen relativ zum Bild
            // (das Bild ist Hochformat ca. 1024x1536, X bei x=47%, y=23/47/70%)
            HotspotMarker(
                xPct = 0.49f, yPct = 0.150f, parentW = w, parentH = h,
                onTap = { pendingMode = GameMode.DUAL_VALLEY }
            )
            HotspotMarker(
                xPct = 0.495f, yPct = 0.435f, parentW = w, parentH = h,
                onTap = { pendingMode = GameMode.TRIAD_OUTPOST }
            )
            HotspotMarker(
                xPct = 0.50f, yPct = 0.700f, parentW = w, parentH = h,
                onTap = { pendingMode = GameMode.BATTLEFIELD_PEAKS }
            )
        }

        // Top-Bar Buttons
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

        IconButton(
            onClick = { showInfo = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .roundCoinButton()
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = stringResource(R.string.menu_info),
                tint = GoldCoinLight
            )
        }
    }

    // Bestätigungs-Popup
    pendingMode?.let { mode ->
        ModeConfirmDialog(
            mode = mode,
            onConfirm = {
                pendingMode = null
                navController.navigate(mode.route)
            },
            onDismiss = { pendingMode = null }
        )
    }

    // Info-Dialog mit Spielregeln
    if (showInfo) {
        InfoDialog(onDismiss = { showInfo = false })
    }
}

// -----------------------------------------------------------------------------
// Hotspot über einem X
// -----------------------------------------------------------------------------

@Composable
private fun HotspotMarker(
    xPct: Float,
    yPct: Float,
    parentW: androidx.compose.ui.unit.Dp,
    parentH: androidx.compose.ui.unit.Dp,
    onTap: () -> Unit
) {
    // Das Bild ist Hochformat – wir berechnen, wie groß es nach Fit
    // wirklich auf dem Screen ist. Aspect Ratio ~ 2:3 (1024x1536).
    val (cx, cy) = HotspotCalculator.computeCenter(
        parentW = parentW.value,
        parentH = parentH.value,
        xPct = xPct,
        yPct = yPct
    )

    val centerX = cx.dp
    val centerY = cy.dp

    // Pulse-Animation für das "Glühen"
    val infinite = rememberInfiniteTransition(label = "hotspotPulse")
    val pulse by infinite.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val hotspotSize = 96.dp
    Box(
        modifier = Modifier
            .offset(
                x = centerX - hotspotSize / 2,
                y = centerY - hotspotSize / 2
            )
            .size(hotspotSize)
            .scale(pulse)
            .clickable(onClick = onTap)
            // Visuelles Feedback: dezenter Glühring (rot wie das X im Bild)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x66FF3030),
                        Color(0x22FF3030),
                        Color.Transparent
                    )
                ),
                shape = CircleShape
            )
    )
}

// -----------------------------------------------------------------------------
// Bestätigungs-Popup
// -----------------------------------------------------------------------------

@Composable
private fun ModeConfirmDialog(
    mode: GameMode,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.menu_choose_battlefield),
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = InkBrown,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 2.sp,
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(mode.nameRes),
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = InkBlack,
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(mode.taglineRes),
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = InkBrown,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.menu_player_count, mode.playerCount),
                    style = TextStyle(fontSize = 13.sp, color = InkBrown, textAlign = TextAlign.Center)
                )
                Spacer(Modifier.height(20.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DialogButton(
                        text = stringResource(R.string.dialog_cancel),
                        primary = false,
                        onClick = onDismiss
                    )
                    DialogButton(
                        text = stringResource(R.string.dialog_march),
                        primary = true,
                        onClick = onConfirm
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// Info-Dialog (Pergament mit Spielregeln)
// -----------------------------------------------------------------------------

@Composable
private fun InfoDialog(onDismiss: () -> Unit) {
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
                    text = stringResource(R.string.info_title),
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = InkBlack
                    )
                )
                Spacer(Modifier.height(12.dp))
                InfoRow(label = stringResource(R.string.info_combat_label),
                    body = stringResource(R.string.info_combat))
                InfoRow(label = stringResource(R.string.info_economy_label),
                    body = stringResource(R.string.info_economy))
                InfoRow(label = stringResource(R.string.info_supply_label),
                    body = stringResource(R.string.info_supply))
                InfoRow(label = stringResource(R.string.info_terrain_label),
                    body = stringResource(R.string.info_terrain))
                Spacer(Modifier.height(16.dp))
                DialogButton(
                    text = stringResource(R.string.dialog_understood),
                    primary = true,
                    onClick = onDismiss
                )
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, body: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = InkBlack,
                letterSpacing = 1.sp
            )
        )
        Text(
            text = body,
            style = TextStyle(fontSize = 13.sp, color = InkBrown)
        )
    }
}

// -----------------------------------------------------------------------------
// Shared Components
// -----------------------------------------------------------------------------

@Composable
internal fun DialogButton(
    text: String,
    primary: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (primary) GoldCoin else WoodMedium,
            contentColor = if (primary) InkBlack else ParchmentLight
        ),
        modifier = Modifier
            .height(46.dp)
            .border(2.dp, GoldCoinDark, RoundedCornerShape(8.dp))
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
        )
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
