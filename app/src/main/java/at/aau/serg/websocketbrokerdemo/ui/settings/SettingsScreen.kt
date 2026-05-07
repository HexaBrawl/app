package at.aau.serg.websocketbrokerdemo.ui.settings

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import at.aau.serg.websocketbrokerdemo.audio.MusicManager
import at.aau.serg.websocketbrokerdemo.data.LocaleCache
import at.aau.serg.websocketbrokerdemo.data.LocaleHelper
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
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = viewModel()
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as? Activity

    /**
     * Wechselt die Sprache und triggert Activity-Recreation,
     * sodass attachBaseContext() neu läuft und alle Composables
     * die neuen Strings ziehen.
     */
    fun changeLanguage(lang: String) {
        if (settings.language == lang) return
        // Synchron ins SharedPrefs spiegeln, damit attachBaseContext es sofort sieht
        LocaleCache.set(context.applicationContext, lang)
        // Persistent in DataStore
        viewModel.setLanguage(lang)
        // Activity neu erstellen -> Sprache greift sofort
        activity?.recreate()
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // Hintergrundbild (etwas kleiner skaliert, dunkler überlegt)
        Image(
            painter = painterResource(id = R.drawable.bg_homescreen),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxSize()
                .background(WoodDark)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.30f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 140.dp)
                .padding(35.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BackButton(onClick = { navController.popBackStack() })
                Spacer(Modifier.width(32.dp))
            }

            // Sprache
            ParchmentCard {
                SectionTitle(stringResource(R.string.settings_language))
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LanguagePill(
                        text = stringResource(R.string.settings_lang_en),
                        selected = settings.language == "en",
                        onClick = { changeLanguage("en") },
                        modifier = Modifier.weight(1f)
                    )
                    LanguagePill(
                        text = stringResource(R.string.settings_lang_de),
                        selected = settings.language == "de",
                        onClick = { changeLanguage("de") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Audio
            ParchmentCard {
                SectionTitle(stringResource(R.string.settings_audio))
                Spacer(Modifier.height(12.dp))

                ToggleRow(
                    label = stringResource(R.string.settings_music),
                    checked = settings.musicEnabled,
                    onCheckedChange = { viewModel.setMusicEnabled(it) }
                )
                Spacer(Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.settings_music_volume),
                    style = TextStyle(fontSize = 16.sp, color = InkBrown, fontWeight = FontWeight.Medium)
                )
                Slider(
                    value = settings.musicVolume,
                    onValueChange = { viewModel.setMusicVolume(it) },
                    enabled = settings.musicEnabled,
                    colors = SliderDefaults.colors(
                        thumbColor = GoldCoin,
                        activeTrackColor = GoldCoinDark,
                        inactiveTrackColor = ParchmentDark
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "${(settings.musicVolume * 100).toInt()}%",
                    style = TextStyle(fontSize = 13.sp, color = InkBrown),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                ToggleRow(
                    label = stringResource(R.string.settings_sfx),
                    checked = settings.sfxEnabled,
                    onCheckedChange = {
                        viewModel.setSfxEnabled(it)
                        if (it) MusicManager.playSwordBlock()
                    }
                )
            }
        }
    }
}

@Composable
private fun BackButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(48.dp)
            .shadow(4.dp, CircleShape)
            .background(
                brush = Brush.radialGradient(listOf(WoodLight, WoodDark)),
                shape = CircleShape
            )
            .border(2.dp, GoldCoinDark, CircleShape)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.settings_back),
            tint = GoldCoinLight
        )
    }
}

@Composable
private fun ParchmentCard(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .background(
                brush = Brush.verticalGradient(listOf(ParchmentLight, ParchmentDark)),
                shape = RoundedCornerShape(12.dp)
            )
            .border(2.dp, GoldCoinDark, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column { content() }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = InkBlack,
            letterSpacing = 1.sp
        )
    )
}

@Composable
private fun LanguagePill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) GoldCoin else WoodMedium,
            contentColor = if (selected) InkBlack else ParchmentLight
        ),
        modifier = modifier
            .height(48.dp)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = GoldCoinDark,
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium
            )
        )
    }
}

@Composable
private fun ToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = TextStyle(fontSize = 18.sp, color = InkBlack, fontWeight = FontWeight.SemiBold)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = GoldCoinLight,
                checkedTrackColor = GoldCoinDark,
                uncheckedThumbColor = ParchmentLight,
                uncheckedTrackColor = WoodMedium,
                checkedBorderColor = GoldCoinDark,
                uncheckedBorderColor = WoodDark
            )
        )
    }
}