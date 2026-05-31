package at.aau.serg.websocketbrokerdemo.ui.settings

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import at.aau.serg.websocketbrokerdemo.data.SettingsRepository
import at.aau.serg.websocketbrokerdemo.data.settingsDataStore
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
class SettingsViewModel(app: Application, private val repo: SettingsRepository) : AndroidViewModel(app) {

    private val logic = SettingsLogic(app)

    constructor(app: Application) : this(
        app,
        SettingsRepository(app.settingsDataStore, app)
    )

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

    fun onLanguageSelected(lang: String, activity: Activity?) {
        val current = state.value.language
        logic.changeLanguage(current, lang, activity)
        viewModelScope.launch { repo.updateLanguage(lang) }
    }

    fun onMusicToggle(enabled: Boolean) {
        viewModelScope.launch { repo.updateMusicEnabled(enabled) }
        logic.applyMusicSettings(enabled, state.value.musicVolume)
    }

    fun onVolumeChanged(volume: Float) {
        viewModelScope.launch { repo.updateMusicVolume(volume) }
        logic.applyMusicSettings(state.value.musicEnabled, volume)
    }

    fun onSfxToggle(enabled: Boolean) {
        viewModelScope.launch { repo.updateSfxEnabled(enabled) }
        logic.applySfxSettings(enabled)
        logic.playSfxIfEnabled(enabled)
    }
}