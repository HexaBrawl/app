package at.aau.serg.websocketbrokerdemo.ui.waiting

import androidx.compose.ui.graphics.Color

/**
 * Standardfarben für Spieler-Wachssiegel.
 *
 * Die Hex-Werte sind etwas gedämpft, damit sie zum Pergament-Look passen
 * (keine Neon-Töne, sondern wie echte Wachssiegel).
 */
enum class PlayerColor(val main: Color, val dark: Color) {
    Red(Color(0xFF8B1A1A), Color(0xFF5A0F0F)),
    Blue(Color(0xFF1F3A6B), Color(0xFF13264A)),
    Green(Color(0xFF2D5A1A), Color(0xFF1A3A0F)),
    Yellow(Color(0xFFC9A227), Color(0xFF8B6F1A))
}

/**
 * Repräsentiert einen Slot in der Wartelobby.
 *
 * - status = Empty:        Niemand drin (zeigt "Warte auf Verbündeten…")
 * - status = Bot/Player:   Belegt, mit Name und Farbe
 *
 * Sobald `ready == true` zeigt das Wachssiegel an dass der Spieler bereit ist.
 */
data class PlayerSlot(
    val id: Int,
    val status: SlotStatus = SlotStatus.Empty,
    val name: String = "",
    val color: PlayerColor = PlayerColor.Red,
    val ready: Boolean = false,
    val isLocal: Boolean = false  // true = der Slot des aktuellen Geräts
)

enum class SlotStatus { Empty, Bot, Player }
