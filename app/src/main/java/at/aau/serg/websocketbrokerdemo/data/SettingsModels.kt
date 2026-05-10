package at.aau.serg.websocketbrokerdemo.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey


object SettingsKeys {
    val LANGUAGE = stringPreferencesKey("language")
    val MUSIC_ENABLED = booleanPreferencesKey("music_enabled")
    val MUSIC_VOLUME = floatPreferencesKey("music_volume")
    val SFX_ENABLED = booleanPreferencesKey("sfx_enabled")
}

data class AppSettings(
    val language: String = "en",
    val musicEnabled: Boolean = true,
    val musicVolume: Float = 0.6f,
    val sfxEnabled: Boolean = true
)
