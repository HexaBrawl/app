package at.aau.serg.websocketbrokerdemo.grid

import androidx.compose.ui.graphics.Color
import at.aau.serg.websocketbrokerdemo.data.serverside.Player
import at.aau.serg.websocketbrokerdemo.data.serverside.PlayerColor

/**
 * Liefert die Compose-Farbe eines Spielers.
 *
 * Frueher per Hash aus dem Namen abgeleitet -- unzuverlaessig, weil
 * zwei Geraete unterschiedliche Hashes haben koennen und auch der
 * gleiche Spieler bei jedem Login eine andere Farbe bekommen koennte.
 *
 * Jetzt: Lookup ueber die Spieler-Liste aus dem GameState. Die
 * `color`-Property im Server-`Player`-Objekt ist die Single Source of
 * Truth -- vom Server gesetzt, fuer alle Geraete identisch.
 *
 * Fallback [DEFAULT_COLOR]: greift nur wenn ein Spieler-Name nicht in
 * der Liste auftaucht (z. B. wenn das GameState noch nicht angekommen
 * ist oder ein "Geist"-Spieler-Namen im Rendering auftaucht).
 */
object PlayerColorMap {

    /** Faellt-zu-Farbe wenn ein Spieler nicht aufloesbar ist. */
    val DEFAULT_COLOR: Color = Color.Gray

    /**
     * Liefert die Compose-Farbe eines Spielers anhand seines Namens.
     *
     * @param playerName Spielername wie vom Server gemeldet
     * @param players    Spieler-Liste aus dem aktuellen GameState
     * @return Compose-Color (PlayerColor.main) oder [DEFAULT_COLOR]
     */
    fun colorFor(playerName: String, players: List<Player>): Color {
        val player = players.firstOrNull { it.name == playerName }
            ?: return DEFAULT_COLOR
        return colorFor(player.color)
    }

    /** Direkt-Mapping von PlayerColor zu Compose-Farbe. */
    fun colorFor(color: PlayerColor): Color = color.main

    /**
     * -subissue #123
     * Liefert die halbtransparente Füll-Farbe für ein erobertes Feld.
     *
     * Gleiche Namens-Auflösung wie [colorFor], nur mit reduziertem
     * Alpha, damit der Karten-Hintergrund unter der Markierung sichtbar
     * bleibt.
     *
     * @param playerName Spielername wie vom Server gemeldet
     * @param players    Spieler-Liste aus dem aktuellen GameState
     * @param alpha      Deckkraft der Fuellung (Default 0.5 = 50 %)
     * @return Spielerfarbe (oder [DEFAULT_COLOR]) mit angewendetem Alpha
     */
    fun cellFillFor(playerName: String, players: List<Player>, alpha: Float = 0.5f): Color =
        colorFor(playerName, players).copy(alpha = alpha)

}
