package at.aau.serg.websocketbrokerdemo.ui.settings

import android.app.Activity
import android.app.Application
import at.aau.serg.websocketbrokerdemo.audio.MusicManager
import at.aau.serg.websocketbrokerdemo.data.LocaleCache

class SettingsLogic(private val app: Application) {

    fun changeLanguage(currentLang: String, newLang: String, activity: Activity?) {
        if (currentLang == newLang) return
        LocaleCache.set(app, newLang)
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