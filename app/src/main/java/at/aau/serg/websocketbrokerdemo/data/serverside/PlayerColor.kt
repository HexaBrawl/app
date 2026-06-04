package at.aau.serg.websocketbrokerdemo.data.serverside

import androidx.compose.ui.graphics.Color

/**
 * Spieler-Farbe -- die zentrale Definition fuer die ganze App.
 *
 * Wurde frueher in zwei separaten Enums gehalten (eines im
 * `data.serverside`-Package mit nur RED/BLUE, eines in
 * `ui.waiting.model` mit Compose-Farbwerten). Beide sind jetzt
 * vereint, damit die in der Wartelobby gewaehlte Farbe ohne Mapping-
 * Schicht bis ins Spiel durchgereicht werden kann.
 *
 * Die Werte entsprechen dem Server-Enum (`RED`, `BLUE`, `GREEN`,
 * `YELLOW`), sodass Gson die JSON-Strings direkt mappen kann. Die
 * `main`/`dark`-Compose-Farben dienen dem Pergament-Wachs-Look in der
 * Lobby und werden spaeter auch fuer die Figuren-Tinting / Burgen-
 * Tinting auf der Karte genutzt.
 */
enum class PlayerColor(val main: Color, val dark: Color) {
    RED(Color(0xFF8B1A1A), Color(0xFF5A0F0F)),
    BLUE(Color(0xFF1F3A6B), Color(0xFF13264A)),
    GREEN(Color(0xFF2D5A1A), Color(0xFF1A3A0F)),
    YELLOW(Color(0xFFC9A227), Color(0xFF8B6F1A))
}
