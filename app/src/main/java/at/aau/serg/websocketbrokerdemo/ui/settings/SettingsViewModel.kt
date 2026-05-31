package at.aau.serg.websocketbrokerdemo.ui.settings

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import at.aau.serg.websocketbrokerdemo.data.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel für den SettingsScreen.
 *
 * Beobachtet das Repository und propagiert Änderungen sofort an den
 * MusicManager. Auch die Sprach-Umschaltung wird hier angestoßen.
 */
class SettingsViewModel(
    app: Application,
    private val repo: SettingsRepository
) : AndroidViewModel(app) {

    private val logic = SettingsLogic(app)

    val state: StateFlow<SettingsState> = repo.settings
        .map { s ->
            SettingsState(
                language = s.language,
                musicEnabled = s.musicEnabled,
                musicVolume = s.musicVolume,
                musicVolumePercent = (s.musicVolume * 100).toInt(),
                sfxEnabled = s.sfxEnabled
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsState()
        )

    // Sprache ändern
    fun onLanguageSelected(lang: String, activity: Activity?) {
        val current = state.value.language
        logic.changeLanguage(current, lang, activity)
        viewModelScope.launch { repo.setLanguage(lang) }
    }

    // Musik an/aus
    fun onMusicToggle(enabled: Boolean) {
        viewModelScope.launch { repo.setMusicEnabled(enabled) }
        logic.applyMusicSettings(enabled, state.value.musicVolume)
    }

    // Musiklautstärke
    fun onVolumeChanged(volume: Float) {
        viewModelScope.launch { repo.setMusicVolume(volume) }
        logic.applyMusicSettings(state.value.musicEnabled, volume)
    }

    // SFX an/aus
    fun onSfxToggle(enabled: Boolean) {
        viewModelScope.launch { repo.setSfxEnabled(enabled) }
        logic.applySfxSettings(enabled)
        logic.playSfxIfEnabled(enabled)
    }
}