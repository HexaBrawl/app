package at.aau.serg.websocketbrokerdemo.data

/**
 * Snapshot der persistenten App-Einstellungen.
 *
 * Wird vom [SettingsRepository] aus dem DataStore ueber die Keys in
 * [SettingsKeys] aufgebaut und vom SettingsViewModel an die UI gereicht.
 * Die Default-Werte spiegeln den Zustand bei Erstinstallation wider —
 * englische Sprache, Musik & SFX an, Musik bei 60 %.
 */
data class AppSettings(
    val language: String = "en",
    val musicEnabled: Boolean = true,
    val musicVolume: Float = 0.6f,
    val sfxEnabled: Boolean = true
)
