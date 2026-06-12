package at.aau.serg.websocketbrokerdemo.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * DataStore-Keys fuer die persistenten App-Einstellungen.
 *
 * Liegen zentral hier, damit Reads und Writes auf die gleichen
 * Key-Instanzen referenzieren. Aenderungen an einem Key (Name oder Typ)
 * sind eine Migration und muessen bewusst durchgefuehrt werden, weil die
 * Strings auch in der von Android persistierten Preference-Datei stehen.
 */
object SettingsKeys {
    val LANGUAGE = stringPreferencesKey("language")
    val MUSIC_ENABLED = booleanPreferencesKey("music_enabled")
    val MUSIC_VOLUME = floatPreferencesKey("music_volume")
    val SFX_ENABLED = booleanPreferencesKey("sfx_enabled")
}
