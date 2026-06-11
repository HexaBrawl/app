package at.aau.serg.websocketbrokerdemo.ui.end

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import at.aau.serg.websocketbrokerdemo.ui.mainmenu.components.DialogButton
import at.aau.serg.websocketbrokerdemo.ui.navigation.Screen
import com.example.myapplication.R

/**
 * EndScreen zeigt das Spielergebnis (Sieg oder Niederlage).
 *
 * Verwendet bg_winscreen oder bg_lossscreen mit Sprach-Suffix als Vollbild-Hintergrund.
 * Am unteren Ende befindet sich ein Button, um zum Home-Screen zurückzukehren.
 */
@Composable
fun EndScreen(
    isWin: Boolean,
    navController: NavController
) {
    val backgroundRes = getEndScreenBackground(isWin)

    Box(modifier = Modifier.fillMaxSize()) {
        // Ergebnis-Hintergrund (Vollbild)
        Image(
            painter = painterResource(id = backgroundRes),
            contentDescription = if (isWin) "Victory" else "Defeat",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        // Button am unteren Rand
        DialogButton(
            text = stringResource(R.string.end_screen_home),
            primary = true,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp),
            onClick = {
                // Zurück zum Home-Screen und Backstack leeren
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
            }
        )
    }
}

/**
 * Wählt das passende Hintergrund-Drawable basierend auf Ergebnis und Sprache.
 */
@Composable
private fun getEndScreenBackground(isWin: Boolean): Int {
    val language = LocalConfiguration.current.locales[0].language

    return when (language) {
        "de" -> if (isWin) R.drawable.bg_winscreen_ger else R.drawable.bg_lossscreen_ger
        "en" -> if (isWin) R.drawable.bg_winscreen_eng else R.drawable.bg_losscreen_eng
        else -> {
            // Fallback auf Englisch
            if (isWin) R.drawable.bg_winscreen_eng else R.drawable.bg_losscreen_eng
        }
    }
}
