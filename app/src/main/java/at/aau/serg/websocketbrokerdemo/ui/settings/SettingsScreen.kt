package at.aau.serg.websocketbrokerdemo.ui.settings

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import at.aau.serg.websocketbrokerdemo.ui.settings.components.LanguageOptionButton
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoin
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinDark
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBrown
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentDark
import at.aau.serg.websocketbrokerdemo.ui.theme.WoodDark
import com.example.myapplication.R
import at.aau.serg.websocketbrokerdemo.ui.settings.components.SettingsBackButton
import at.aau.serg.websocketbrokerdemo.ui.settings.components.SettingsBackground
import at.aau.serg.websocketbrokerdemo.ui.settings.components.SettingsOptionSwitch
import at.aau.serg.websocketbrokerdemo.ui.settings.components.SettingsSectionTitle


@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as? Activity

    Box(modifier = Modifier.fillMaxSize()) {

        // Hintergrundbild
        Image(
            painter = painterResource(id = R.drawable.bg_homescreen),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxSize()
                .background(WoodDark)
        )

        // Abdunkelung
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

            // Back Button
            Row(verticalAlignment = Alignment.CenterVertically) {
                SettingsBackButton(onClick = { navController.popBackStack() })
                Spacer(Modifier.width(32.dp))
            }

            // Sprache
            SettingsBackground {
                SettingsSectionTitle(stringResource(R.string.settings_language))
                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LanguageOptionButton(
                        text = stringResource(R.string.settings_lang_en),
                        selected = state.language == "en",
                        onClick = { viewModel.onLanguageSelected("en", activity) },
                        modifier = Modifier.weight(1f)
                    )

                    LanguageOptionButton(
                        text = stringResource(R.string.settings_lang_de),
                        selected = state.language == "de",
                        onClick = { viewModel.onLanguageSelected("de", activity) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Audio
            SettingsBackground {
                SettingsSectionTitle(stringResource(R.string.settings_audio))
                Spacer(Modifier.height(12.dp))

                // Musik an/aus
                SettingsOptionSwitch(
                    label = stringResource(R.string.settings_music),
                    checked = state.musicEnabled,
                    onCheckedChange = { viewModel.onMusicToggle(it) }
                )

                Spacer(Modifier.height(12.dp))

                // Musiklautstärke
                Text(
                    text = stringResource(R.string.settings_music_volume),
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = InkBrown,
                        fontWeight = FontWeight.Medium
                    )
                )

                Slider(
                    value = state.musicVolume,
                    onValueChange = { viewModel.onVolumeChanged(it) },
                    enabled = state.musicEnabled,
                    colors = SliderDefaults.colors(
                        thumbColor = GoldCoin,
                        activeTrackColor = GoldCoinDark,
                        inactiveTrackColor = ParchmentDark
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "${state.musicVolumePercent}%",
                    style = TextStyle(fontSize = 13.sp, color = InkBrown),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                // SFX an/aus
                SettingsOptionSwitch(
                    label = stringResource(R.string.settings_sfx),
                    checked = state.sfxEnabled,
                    onCheckedChange = { viewModel.onSfxToggle(it) }
                )
            }
        }
    }
}