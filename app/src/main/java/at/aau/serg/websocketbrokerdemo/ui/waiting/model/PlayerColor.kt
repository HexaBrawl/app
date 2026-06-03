package at.aau.serg.websocketbrokerdemo.ui.waiting.model

import androidx.compose.ui.graphics.Color

/**
 * Standardfarben fuer Spieler-Wachssiegel in der Wartelobby.
 *
 * Die Hex-Werte sind etwas gedaempft, damit sie zum Pergament-Look
 * passen (keine Neon-Toene, sondern wie echte Wachssiegel). Jeder
 * Spieler-Slot bekommt eine eindeutige PlayerColor zugewiesen.
 *
 * `main` ist die aeussere/hellere Farbe, `dark` die innere/dunklere
 * fuer den radialen Verlauf des Siegels.
 */
enum class PlayerColor(val main: Color, val dark: Color) {
    Red(Color(0xFF8B1A1A), Color(0xFF5A0F0F)),
    Blue(Color(0xFF1F3A6B), Color(0xFF13264A)),
    Green(Color(0xFF2D5A1A), Color(0xFF1A3A0F)),
    Yellow(Color(0xFFC9A227), Color(0xFF8B6F1A))
}
