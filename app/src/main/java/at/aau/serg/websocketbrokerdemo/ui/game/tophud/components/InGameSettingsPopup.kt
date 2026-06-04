package at.aau.serg.websocketbrokerdemo.ui.game.tophud.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import at.aau.serg.websocketbrokerdemo.ui.settings.SettingsViewModel
import at.aau.serg.websocketbrokerdemo.ui.settings.components.SettingsBackground
import at.aau.serg.websocketbrokerdemo.ui.settings.components.SettingsOptionSwitch
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoin
import at.aau.serg.websocketbrokerdemo.ui.theme.GoldCoinDark
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBlack
import at.aau.serg.websocketbrokerdemo.ui.theme.InkBrown
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentDark
import at.aau.serg.websocketbrokerdemo.ui.theme.ParchmentLight
import com.example.myapplication.R

/**
 * In-Game-Einstellungs-Popup.
 *
 * Im Gegensatz zum vollen SettingsScreen enthaelt dieser nur die
 * Audio-Einstellungen (Musik an/aus + Lautstaerke, SFX an/aus). Sprache
 * wird hier bewusst weggelassen, weil eine Sprachumschaltung mitten im
 * Spiel die Activity neu starten wuerde.
 *
 * Nutzt dasselbe [SettingsViewModel] wie der SettingsScreen, damit
 * Aenderungen sofort am MusicManager wirken (nicht nur in DataStore
 * landen). Da das ViewModel ein AndroidViewModel ist, kommt die
 * Application-Referenz automatisch von Compose ueber viewModel().
 */
@Composable
fun InGameSettingsPopup(
    onDismiss: () -> Unit
) {
    val viewModel: SettingsViewModel = viewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    Dialog(onDismissRequest = onDismiss) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(20.dp, RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.verticalGradient(listOf(ParchmentLight, ParchmentDark)),
                    shape = RoundedCornerShape(16.dp)
                )
                .border(3.dp, GoldCoinDark, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.hud_settings_title),
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = InkBlack
                )
            )

            Spacer(Modifier.height(20.dp))

            // Audio
            SettingsBackground {

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