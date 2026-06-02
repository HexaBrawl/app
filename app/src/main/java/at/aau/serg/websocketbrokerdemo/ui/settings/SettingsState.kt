package at.aau.serg.websocketbrokerdemo.ui.settings

data class SettingsState(
    val language: String = "en",
    val musicEnabled: Boolean = true,
    val musicVolume: Float = 1f,
    val musicVolumePercent: Int = 100,
    val sfxEnabled: Boolean = true
)