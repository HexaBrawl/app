package at.aau.serg.websocketbrokerdemo.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import at.aau.serg.websocketbrokerdemo.audio.MusicManager
import at.aau.serg.websocketbrokerdemo.data.AppSettings
import at.aau.serg.websocketbrokerdemo.data.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel für den SettingsScreen.
 *
 * Beobachtet das Repository und propagiert Änderungen sofort an den
 * MusicManager. Auch die Sprach-Umschaltung wird hier angestoßen.
 */
class SettingsViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = SettingsRepository(app)

    val settings: StateFlow<AppSettings> = repo.settings
        .onEach { s ->
            // Audio-Settings unmittelbar anwenden
            MusicManager.applyMusicSettings(s.musicEnabled, s.musicVolume)
            MusicManager.applySfxSettings(s.sfxEnabled)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppSettings()
        )

    fun setLanguage(lang: String) = viewModelScope.launch { repo.setLanguage(lang) }
    fun setMusicEnabled(enabled: Boolean) = viewModelScope.launch { repo.setMusicEnabled(enabled) }
    fun setMusicVolume(volume: Float) = viewModelScope.launch { repo.setMusicVolume(volume) }
    fun setSfxEnabled(enabled: Boolean) = viewModelScope.launch { repo.setSfxEnabled(enabled) }
}
