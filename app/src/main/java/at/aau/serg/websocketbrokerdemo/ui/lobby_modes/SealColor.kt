package at.aau.serg.websocketbrokerdemo.ui.lobby_modes

import androidx.compose.ui.graphics.Color

/**
 * Farben fuer die Wachs-Siegel der ActionCards in der Modus-Lobby.
 *
 * Jedes Siegel hat einen helleren Haupt-Ton (`main`) fuer das aeussere
 * Glanzlicht und einen dunkleren Schatten-Ton (`dark`) fuer den
 * radialen Verlauf -- so wirken die Siegel plastisch und passen zum
 * Pergament-/Holz-Look des Hauptmenues.
 *
 *  - [Red]   fuer die "Privates Spiel erstellen"-Aktion (Auffaellig, Initiative)
 *  - [Blue]  fuer "Spiel beitreten" (Ruhig, geladenes Wachs)
 *  - [Gold]  fuer "Zufaelliges Spiel" (Schicksal, Wuerfel)
 */
enum class SealColor(val main: Color, val dark: Color) {
    Red(Color(0xFF8B1A1A), Color(0xFF5A0F0F)),
    Blue(Color(0xFF1F3A6B), Color(0xFF13264A)),
    Gold(Color(0xFFD4A24C), Color(0xFF9C6F22))
}
