package at.aau.serg.websocketbrokerdemo.ui.settings

/**
 * Beobachtbarer UI-State des Einstellungs-Screens.
 *
 * Wird vom SettingsViewModel gehalten und vom Screen gerendert.
 * [musicVolume] ist der 0..1-Faktor fuer den Player, [musicVolumePercent]
 * der daraus abgeleitete Prozentwert fuer die Anzeige.
 */
data class SettingsState(
    val language: String = "en",
    val musicEnabled: Boolean = true,
    val musicVolume: Float = 1f,
    val musicVolumePercent: Int = 100,
    val sfxEnabled: Boolean = true
)