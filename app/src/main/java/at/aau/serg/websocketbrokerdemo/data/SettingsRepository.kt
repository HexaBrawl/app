package at.aau.serg.websocketbrokerdemo.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Persistente App-Einstellungen via DataStore.
 *
 * Hält:
 *  - Sprache ("de" / "en")
 *  - Musik on/off + Lautstärke (0.0 - 1.0)
 *  - SFX (Soundeffekte) on/off
 *
 * Wird in einem späteren Task ggf. um weitere Felder ergänzt
 * (z. B. Spielername, Server-URL).
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "hexabrawl_settings")

object SettingsKeys {
    val LANGUAGE = stringPreferencesKey("language")        // "de" | "en"
    val MUSIC_ENABLED = booleanPreferencesKey("music_enabled")
    val MUSIC_VOLUME = floatPreferencesKey("music_volume") // 0f..1f
    val SFX_ENABLED = booleanPreferencesKey("sfx_enabled")
}

data class AppSettings(
    val language: String = "en",
    val musicEnabled: Boolean = true,
    val musicVolume: Float = 0.6f,
    val sfxEnabled: Boolean = true
)

class SettingsRepository(private val context: Context) {

    val settings: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            language = prefs[SettingsKeys.LANGUAGE] ?: defaultLanguage(),
            musicEnabled = prefs[SettingsKeys.MUSIC_ENABLED] ?: true,
            musicVolume = prefs[SettingsKeys.MUSIC_VOLUME] ?: 0.6f,
            sfxEnabled = prefs[SettingsKeys.SFX_ENABLED] ?: true
        )
    }

    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { it[SettingsKeys.LANGUAGE] = lang }
    }

    suspend fun setMusicEnabled(enabled: Boolean) {
        context.dataStore.edit { it[SettingsKeys.MUSIC_ENABLED] = enabled }
    }

    suspend fun setMusicVolume(volume: Float) {
        context.dataStore.edit { it[SettingsKeys.MUSIC_VOLUME] = volume.coerceIn(0f, 1f) }
    }

    suspend fun setSfxEnabled(enabled: Boolean) {
        context.dataStore.edit { it[SettingsKeys.SFX_ENABLED] = enabled }
    }

    /** Defaultsprache anhand der System-Locale wählen. */
    private fun defaultLanguage(): String {
        val sys = java.util.Locale.getDefault().language
        return if (sys == "de") "de" else "en"
    }
}
