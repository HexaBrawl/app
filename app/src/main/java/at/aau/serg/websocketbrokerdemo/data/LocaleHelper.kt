package at.aau.serg.websocketbrokerdemo.data

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

/**
 * Setzt die App-Sprache zur Laufzeit.
 *
 * Nutzt die Per-App-Language-API (AppCompat 1.6+).
 * - Auf Android 13+ ist das systemweit integriert
 * - Auf älteren Versionen wird die Sprache app-intern persistiert
 *
 * Wichtig: Das löst eine Activity-Recreation aus, damit alle
 * Composables neue Strings ziehen.
 */
object LocaleHelper {

    fun apply(language: String) {
        val tag = when (language) {
            "de" -> "de"
            "en" -> "en"
            else -> "en"
        }
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
    }
}
