package at.aau.serg.websocketbrokerdemo.ui.home

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinDark
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinLight
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentLight
import at.aau.serg.websocketbrokerdemo.ui.theme.WoodDark
import at.aau.serg.websocketbrokerdemo.ui.theme.WoodLight
import at.aau.serg.websocketbrokerdemo.ui.theme.WoodMedium
import com.example.myapplication.R

/**
 * HomeScreen -- der Startbildschirm der App.
 *
 * Reine UI-Schicht. Sämtliche Logik (Musik starten, Navigation, Exit)
 * liegt in [HomeScreenLogic] und ist dort getestet.
 */
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val activity = context as? Activity

    LaunchedEffect(Unit) {
        HomeScreenLogic.startMenuMusic(context)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // 1) Hintergrundbild
        Image(
            painter = painterResource(id = R.drawable.bg_homescreen),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        // 3) Sanfte Vignette
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

        // 4) Logo oben mittig — etwas tiefer und groesser, sodass die
        // untere Spitze in Hoehe des Settings-Buttons sitzt und der
        // Button optisch davon "haengt".
        Image(
            painter = painterResource(id = R.drawable.start_logo),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 24.dp)
                .height(220.dp)
        )

        // 5) Settings-Button oben
        IconButton(
            onClick = { HomeScreenLogic.onSettingsClicked(navController) },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 155.dp)
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

        // 6) PLAY Button mittig
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 10.dp)
                .size(300.dp)
        ) {
            Button(
                onClick = { HomeScreenLogic.onPlayClicked(navController) },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Unspecified
                ),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.playbutton),
                    contentDescription = "Play",
                    modifier = Modifier.fillMaxSize(),
                    tint = Color.Unspecified
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
                onClick = { HomeScreenLogic.exitApp(activity) },
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