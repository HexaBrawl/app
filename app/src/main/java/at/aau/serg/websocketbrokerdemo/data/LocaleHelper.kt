package at.aau.serg.websocketbrokerdemo.data

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

/**
 * App-interne Sprachumschaltung — komplett ohne System-Einstellungen.
 *
 * Funktioniert so:
 *  1. Beim Start liest die MainActivity in attachBaseContext() die gespeicherte
 *     Sprache aus DataStore und ruft updateLocale(base, lang) auf, um den
 *     Activity-Context zu wrappen.
 *  2. Bei einem Sprachwechsel im SettingsScreen wird die Activity recreated,
 *     wodurch attachBaseContext() erneut aufgerufen wird und die neue Sprache
 *     greift.
 *
 * Der Vorteil gegenüber AppCompatDelegate.setApplicationLocales:
 *  - Kein Eintrag in den System-Einstellungen
 *  - Kein localeConfig.xml im Manifest nötig
 *  - Funktioniert identisch auf allen Android-Versionen
 */
object LocaleHelper {

    /** Wrappt den gegebenen Context, sodass alle Resources die Ziel-Locale nutzen. */
    fun updateLocale(context: Context, language: String): Context {
        val locale = Locale(normalize(language))
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        return context.createConfigurationContext(config)
    }

    private fun normalize(language: String): String = when (language) {
        "de" -> "de"
        "en" -> "en"
        else -> "en"
    }
}