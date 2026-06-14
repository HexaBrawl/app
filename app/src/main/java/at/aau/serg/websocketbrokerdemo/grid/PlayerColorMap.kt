package at.aau.serg.websocketbrokerdemo.grid

import androidx.compose.ui.graphics.Color
import at.aau.serg.websocketbrokerdemo.data.serverside.Player
import at.aau.serg.websocketbrokerdemo.data.serverside.PlayerColor

/**
 * Liefert die Compose-Farbe eines Spielers.
 *
 * Lookup ueber die Spieler-Liste aus dem GameState. Die `color`-Property
 * im Server-`Player` ist die Single Source of Truth.
 */
object PlayerColorMap {

    /** Fallback wenn ein Spieler nicht aufloesbar ist. */
    val DEFAULT_COLOR: Color = Color.Gray

    /**
     * Anteil Grau in der Skelett-Fuellfarbe (#172).
     * 0.7 = 70 % Grau, 30 % Spielerfarbe -- deutlich sichtbar auf
     * Pergament + 50 % Alpha, Hue bleibt aber unterscheidbar.
     */
    private const val GRAY_MIX_FACTOR: Float = 0.7f

    fun colorFor(playerName: String, players: List<Player>): Color {
        val player = players.firstOrNull { it.name == playerName }
            ?: return DEFAULT_COLOR
        return colorFor(player.color)
    }

    fun colorFor(color: PlayerColor): Color = color.main

    /**
     * Halbtransparente Fuell-Farbe fuer ein erobertes (lebendes) Feld.
     */
    fun cellFillFor(playerName: String, players: List<Player>, alpha: Float = 0.5f): Color =
        colorFor(playerName, players).copy(alpha = alpha)

    /**
     * Halbtransparente Fuell-Farbe fuer ein abgeschnittenes (Skelett-)
     * Feld. Spielerfarbe wird mit [Color.Gray] gemischt (Anteil
     * [GRAY_MIX_FACTOR]); Alpha analog zu [cellFillFor].
     */
    fun skeletonCellFillFor(
        playerName: String,
        players: List<Player>,
        alpha: Float = 0.5f
    ): Color {
        val base = colorFor(playerName, players)
        return mixWithGray(base, GRAY_MIX_FACTOR).copy(alpha = alpha)
    }

    /** Linearer sRGB-Mix; f=0 → unveraendert, f=1 → reines Grau. */
    private fun mixWithGray(color: Color, factor: Float): Color {
        val gray = Color.Gray
        val keep = 1f - factor
        return Color(
            red = color.red * keep + gray.red * factor,
            green = color.green * keep + gray.green * factor,
            blue = color.blue * keep + gray.blue * factor,
            alpha = 1f
        )
    }
}