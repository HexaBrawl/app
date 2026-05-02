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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
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




@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val activity = context as? Activity

    LaunchedEffect(Unit) {
        MusicManager.playMenuMusic(context)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // 1) Hintergrund-Holzfläche, sichtbar an den Rändern wo das Bild aufhört
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

        // 2) Hintergrundbild – komplett sichtbar (Fit), leicht herauszoomen via scale
        //
        //    ContentScale.Fit:  Bild komplett zeigen, ggf. Ränder leer (gelöst durch
        //                       die Holz-Box darunter).
        //    scale(0.92f):      Optisches "weiter weg" – kannst du anpassen:
        //                       1.0f  = Bild füllt soviel wie möglich
        //                       0.85f = deutlich kleiner, mehr Holzrahmen sichtbar
        //                       0.95f = ganz leicht zurückgenommen
        Image(
            painter = painterResource(id = R.drawable.bg_homescreen),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .scale(1.25f)
        )

        // 3) Sanfte Vignette für Tiefe und besseren Button-Kontrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.25f)
                        ),
                        radius = 1400f
                    )
                )
        )

        // 4) Settings-Button oben rechts
        IconButton(
            onClick = { navController.navigate("settings") },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(40.dp)
                .size(56.dp)
                .shadow(6.dp, CircleShape)
                .background(
                    brush = Brush.radialGradient(listOf(WoodLight, WoodDark)),
                    shape = CircleShape
                )
                .border(2.dp, GoldCoinDark, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = stringResource(R.string.home_settings),
                tint = GoldCoinLight,
                modifier = Modifier.size(28.dp)
            )
        }

        // 5) PLAY mittig als Goldmünze
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .size(250.dp)
                .shadow(elevation = 14.dp, shape = CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(GoldCoinLight, GoldCoin, GoldCoinDark),
                        radius = 280f
                    ),
                    shape = CircleShape
                )
                .border(width = 4.dp, color = GoldCoinDark, shape = CircleShape)
        ) {
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .border(width = 1.5.dp, color = GoldCoinDark, shape = CircleShape)
            )
            Button(
                onClick = {
                    navigateSafe(navController, primary = "mainmenu", fallback = "game")
                },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = InkBlack
                ),
                modifier = Modifier.size(220.dp)
            ) {
                Text(
                    text = stringResource(R.string.home_play),
                    style = TextStyle(
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = InkBlack,
                        letterSpacing = 3.sp
                    )
                )
            }
        }

        // 6) Exit unten mittig
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 62.dp)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(8.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(WoodLight, WoodMedium, WoodDark)
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                .border(2.dp, GoldCoinDark, RoundedCornerShape(8.dp))
        ) {
            Button(
                onClick = { activity?.finish() },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = ParchmentLight
                ),
                modifier = Modifier.height(54.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ExitToApp,
                    contentDescription = null,
                    tint = GoldCoinLight,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.home_exit),
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
}

private fun navigateSafe(navController: NavController, primary: String, fallback: String) {
    val graph = navController.graph
    val hasPrimary = graph.any { it.route == primary }
    navController.navigate(if (hasPrimary) primary else fallback)
}