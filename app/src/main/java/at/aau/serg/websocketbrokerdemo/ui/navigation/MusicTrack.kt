package at.aau.serg.websocketbrokerdemo.ui.navigation

/**
 * Die Audio-Tracks der App, gruppiert nach Spiel-Kontext.
 *
 * Jeder Screen gehört zu genau einem MusicTrack. Die Zuordnung erfolgt
 * in [NavigationLogic.trackForRoute]; der MusicTrack-Wert wird dann vom
 * AppNavHost in einen MusicManager-Aufruf übersetzt.
 *
 * Diese Indirektion existiert, damit die "Route -> Track"-Logik testbar
 * bleibt -- direkter MusicManager-Aufruf wäre an den Compose-Code
 * gebunden und nicht in Unit-Tests prüfbar.
 */
enum class MusicTrack {

    /** Ruhige Menü-Musik (Home, Settings, MainMenu, Mode-Lobbys). */
    Menu,

    /** Spannungsvolle Turnier-Musik (Wartelobby). */
    Tournament,

    /** Kampf-Musik (im Spiel). */
    Battle
}
