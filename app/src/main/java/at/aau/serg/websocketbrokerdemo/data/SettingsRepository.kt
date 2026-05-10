package at.aau.serg.websocketbrokerdemo.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(private val dataStore: DataStore<Preferences>,
                         private val context: Context
) {

    val settings: Flow<AppSettings> = dataStore.data.map { prefs ->
        AppSettings(
            language = prefs[SettingsKeys.LANGUAGE] ?: LocaleCache.get(context),
            musicEnabled = prefs[SettingsKeys.MUSIC_ENABLED] ?: true,
            musicVolume = prefs[SettingsKeys.MUSIC_VOLUME] ?: 0.6f,
            sfxEnabled = prefs[SettingsKeys.SFX_ENABLED] ?: true
        )
    }

    suspend fun setLanguage(lang: String) {
        // 1) Persistent in DataStore
        dataStore.edit { it[SettingsKeys.LANGUAGE] = lang }
        // 2) Synchroner Spiegel für attachBaseContext()
        LocaleCache.set(context, lang)
    }

    suspend fun setMusicEnabled(enabled: Boolean) {
        dataStore.edit { it[SettingsKeys.MUSIC_ENABLED] = enabled }
    }

    suspend fun setMusicVolume(volume: Float) {
        dataStore.edit { it[SettingsKeys.MUSIC_VOLUME] = volume.coerceIn(0f, 1f) }
    }

    suspend fun setSfxEnabled(enabled: Boolean) {
        dataStore.edit { it[SettingsKeys.SFX_ENABLED] = enabled }
    }
}