package at.aau.serg.websocketbrokerdemo.ui.lobby

import android.app.Activity
import android.graphics.drawable.Icon
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import at.aau.serg.websocketbrokerdemo.audio.MusicManager
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoin
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinDark
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinLight
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBlack
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBrown
import at.aau.serg.websocketbrokerdemo.ui.theme.InkFaded
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentBase
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentDark
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentEdge
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentLight
import at.aau.serg.websocketbrokerdemo.ui.theme.WoodDark
import at.aau.serg.websocketbrokerdemo.ui.theme.WoodLight
import at.aau.serg.websocketbrokerdemo.ui.theme.WoodMedium
import com.example.myapplication.R


/**
 * HomeScreen im "Kingdom Chronicles"-Stil.
 *
 * Drei vertikale Sektionen:
 *   - Oben:   Settings-Button (Sprache, Lautstärke)
 *   - Mitte:  prominenter PLAY-Button -> navigiert zum Hauptmenü
 *   - Unten:  Exit-Button
 *
 * Die Hintergrundmusik (Menü-Track) startet hier und läuft durch das gesamte
 * Hauptmenü; sie wird erst pausiert, wenn der Spieler ins Kampfsystem wechselt.
 */
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val activity = context as? Activity

    // Menü-Musik starten, sobald der Homescreen gezeigt wird.
    LaunchedEffect(Unit) {
        MusicManager.playMenuMusic(context)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .parchmentBackground()
    ) {
        // Tintenflecken-Rand als dekorative Ebene
        ParchmentInkBorder(modifier = Modifier.fillMaxSize())

        // ----- Sektion OBEN: Settings -----
        IconButton(
            onClick = {
                // TODO: navController.navigate("settings") – kommt in einem späteren Task
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(20.dp)
                .size(56.dp)
                .shadow(4.dp, CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(WoodLight, WoodDark)
                    ),
                    shape = CircleShape
                )
                .border(2.dp, GoldCoinDark, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "Settings",
                tint = GoldCoinLight,
                modifier = Modifier.size(28.dp)
            )
        }

        // ----- Drei vertikale Sektionen -----
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Sektion 1: Titel-Bereich (oben Mitte, neben dem Settings-Button)
            TitleBanner()

            // Sektion 2: PLAY (Mitte) – prominent als Goldmünze
            PlayButton(
                onClick = {
                    // Navigiert zum Hauptmenü (kommt in einem späteren Task).
                    // Solange "mainmenu" noch nicht registriert ist, fällt der Klick
                    // auf "game" zurück, damit nichts crasht.
                    navigateSafe(navController, primary = "mainmenu", fallback = "game")
                }
            )

            // Sektion 3: Exit (unten)
            ExitButton(
                onClick = { activity?.finish() }
            )
        }
    }
}

@Composable
private fun TitleBanner() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 16.dp)
    ) {
        Text(
            text = "Welcome to",
            style = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = InkBrown,
                textAlign = TextAlign.Center
            )
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "HEXABRAWL",
            style = TextStyle(
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                color = InkBlack,
                textAlign = TextAlign.Center,
                letterSpacing = 4.sp
            )
        )
        Spacer(Modifier.height(6.dp))
        // Verzierte Linie (Federstrich) unter dem Titel
        Canvas (
            modifier = Modifier
                .width(220.dp)
                .height(12.dp)
        ) {
            val cy = size.height / 2
            drawLine(
                color = InkBlack,
                start = Offset(0f, cy),
                end = Offset(size.width, cy),
                strokeWidth = 1.5f
            )
            // Kleine Rauten als Ornament
            val midX = size.width / 2
            drawLine(InkBlack, Offset(midX - 6, cy - 4), Offset(midX, cy), 1.5f)
            drawLine(InkBlack, Offset(midX, cy), Offset(midX + 6, cy - 4), 1.5f)
            drawLine(InkBlack, Offset(midX - 6, cy + 4), Offset(midX, cy), 1.5f)
            drawLine(InkBlack, Offset(midX, cy), Offset(midX + 6, cy + 4), 1.5f)
        }
    }
}

@Composable
private fun PlayButton(onClick: () -> Unit) {
    // PLAY als prachtvolle Goldmünze (passt zur "Hauptgebäude"-Identität)
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(220.dp)
            .shadow(elevation = 12.dp, shape = CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(GoldCoinLight, GoldCoin, GoldCoinDark),
                    radius = 320f
                ),
                shape = CircleShape
            )
            .border(width = 4.dp, color = GoldCoinDark, shape = CircleShape)
    ) {
        // Innerer Münzring (Wachssiegel-Optik)
        Box(
            modifier = Modifier
                .size(190.dp)
                .border(width = 1.5.dp, color = GoldCoinDark, shape = CircleShape)
        )
        Button(
            onClick = onClick,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = InkBlack
            ),
            modifier = Modifier.size(180.dp)
        ) {
            Text(
                text = "PLAY",
                style = TextStyle(
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = InkBlack,
                    letterSpacing = 3.sp
                )
            )
        }
    }
}

@Composable
private fun ExitButton(onClick: () -> Unit) {
    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(bottom = 16.dp)
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(8.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(WoodLight, WoodMedium, WoodDark)
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .border(2.dp, GoldCoinDark, RoundedCornerShape(8.dp))
    ) {
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = ParchmentLight
            ),
            modifier = Modifier.height(54.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Cancel,
                contentDescription = null,
                tint = GoldCoinLight,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = "Exit",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ParchmentLight,
                    letterSpacing = 2.sp
                )
            )
        }
    }
}

private fun Modifier.parchmentBackground(): Modifier = this.background(
    brush = Brush.radialGradient(
        colors = listOf(ParchmentLight, ParchmentBase, ParchmentDark),
        radius = 1400f
    )
)

@Composable
private fun ParchmentInkBorder(modifier: Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Dunkler Rand (gealterte Pergament-Kante)
        drawRect(
            color = ParchmentEdge.copy(alpha = 0.25f),
            topLeft = Offset(0f, 0f),
            size = size,
            style = Stroke(width = 24f)
        )

        // Ein paar "Tintenflecken" – semi-transparente Kreise
        val splotches = listOf(
            Triple(0.05f, 0.08f, 60f),
            Triple(0.92f, 0.12f, 45f),
            Triple(0.04f, 0.55f, 38f),
            Triple(0.96f, 0.62f, 55f),
            Triple(0.10f, 0.93f, 50f),
            Triple(0.88f, 0.95f, 42f),
            Triple(0.50f, 0.04f, 30f),
            Triple(0.50f, 0.97f, 32f)
        )
        splotches.forEach { (xPct, yPct, radius) ->
            drawCircle(
                color = InkFaded.copy(alpha = 0.18f),
                radius = radius,
                center = Offset(w * xPct, h * yPct)
            )
            drawCircle(
                color = InkBlack.copy(alpha = 0.10f),
                radius = radius * 0.5f,
                center = Offset(w * xPct, h * yPct)
            )
        }
    }
}

/**
 * Versucht zur primären Route zu navigieren; falls die noch nicht registriert
 * ist (NavGraph wird in späteren Tasks erweitert), wird der Fallback genutzt.
 */
private fun navigateSafe(navController: NavController, primary: String, fallback: String) {
    val graph = navController.graph
    val hasPrimary = graph.any { it.route == primary }
    navController.navigate(if (hasPrimary) primary else fallback)
}