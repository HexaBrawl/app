package at.aau.serg.websocketbrokerdemo.data

import android.content.Context

/**
 * Kleiner synchroner Cache für die Sprache.
 *
 * Hintergrund: DataStore ist coroutinen-basiert und somit async.
 * In Activity.attachBaseContext() läuft aber noch keine Coroutine,
 * wir brauchen die Sprache aber **sofort**, um den Context zu wrappen.
 *
 * Lösung: Wir spiegeln die Sprache zusätzlich in SharedPreferences,
 * was synchron lesbar ist. Die Single-Source-of-Truth bleibt DataStore.
 */
object LocaleCache {
    private const val PREFS = "hexabrawl_locale_cache"
    private const val KEY_LANG = "language"

    fun get(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANG, defaultLanguage()) ?: "en"
    }

    fun set(context: Context, language: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LANG, language)
            .apply()
    }

    private fun defaultLanguage(): String {
        val sys = java.util.Locale.getDefault().language
        return if (sys == "de") "de" else "en"
    }
}
