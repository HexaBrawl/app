package at.aau.serg.websocketbrokerdemo.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/**
 * App-weiter Zugriff auf den Preferences-[DataStore] fuer Settings.
 *
 * Als Context-Extension definiert, sodass jede Komponente mit Context den
 * Store ueber `context.settingsDataStore` erreicht. Der DataStore wird vom
 * Delegate genau einmal pro Prozess unter dem Namen "hexabrawl_settings"
 * angelegt; das [SettingsRepository] liest und schreibt darueber die
 * persistenten Einstellungen (Sprache, Lautstaerken, ...).
 */

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "hexabrawl_settings")