package at.aau.serg.websocketbrokerdemo.ui.settings

import android.app.Activity
import android.app.Application
import at.aau.serg.websocketbrokerdemo.audio.MusicManager
import at.aau.serg.websocketbrokerdemo.data.LanguageCache

/**
 * Reine Aktions-Logik des Einstellungs-Screens.
 *
 * Kapselt die Seiteneffekte hinter den Settings-Eingaben, damit das
 * ViewModel/Composable sie nur ausloest: Sprachwechsel (Cache setzen +
 * Activity neu erstellen) sowie das Anwenden von Musik-/SFX-Einstellungen
 * ueber den [MusicManager].
 */
class SettingsLogic(private val app: Application) {

    fun changeLanguage(currentLang: String, newLang: String, activity: Activity?) {
        if (currentLang == newLang) return
        LanguageCache.set(app, newLang)
        activity?.recreate()
    }

    fun applyMusicSettings(enabled: Boolean, volume: Float) {
        MusicManager.applyMusicSettings(enabled, volume)
    }

    fun applySfxSettings(enabled: Boolean) {
        MusicManager.applySfxSettings(enabled)
    }

    fun playSfxIfEnabled(enabled: Boolean) {
        if (enabled) {
            MusicManager.playSwordBlock()
        }

    }
}