package at.aau.serg.websocketbrokerdemo.grid

import at.aau.serg.websocketbrokerdemo.ui.mainmenu.GameMode

/**
 * Vordefinierte Map-Layouts pro Spielmodus.
 *
 * Mehr Spieler -> groesseres Brett, aber kleinere Hexes, damit das
 * Brett unabhaengig vom Modus aehnlich gross auf dem Bildschirm
 * erscheint.
 *
 * Ersetzt das frueher player-count-basierte GridLibrary.forPlayers(Int);
 * die typsichere GameMode-Variante macht den Aufrufer-Code lesbarer und
 * verhindert Magic Numbers ueberall.
 */
object MapLayouts {

    val DUAL_VALLEY = MapLayout(
        rows = 10,
        cols = 10,
        hexSize = 60f,
        name = "Dual Valley"
    )

    val TRIAD_OUTPOST = MapLayout(
        rows = 12,
        cols = 12,
        hexSize = 50f,
        name = "Triad Outpost"
    )

    val BATTLEFIELD_PEAKS = MapLayout(
        rows = 14,
        cols = 14,
        hexSize = 45f,
        name = "Battlefield Peaks"
    )

    /**
     * Liefert das Map-Layout fuer den gegebenen Spielmodus.
     *
     * Das `when` ist exhaustive ueber den GameMode-Enum -- wenn jemand
     * einen neuen Modus dazu nimmt, erinnert der Compiler hier daran,
     * auch ein passendes MapLayout zu definieren.
     */
    fun forMode(mode: GameMode): MapLayout = when (mode) {
        GameMode.DUAL_VALLEY -> DUAL_VALLEY
        GameMode.TRIAD_OUTPOST -> TRIAD_OUTPOST
        GameMode.BATTLEFIELD_PEAKS -> BATTLEFIELD_PEAKS
    }
}
