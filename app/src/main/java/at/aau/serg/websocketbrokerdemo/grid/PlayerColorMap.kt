package at.aau.serg.websocketbrokerdemo.grid

import android.graphics.Color

/**
 * Stabile, deterministische Zuordnung von Spieler-Name zu Farbe.
 *
 * Wichtig: Die Zuordnung muss reproduzierbar sein -- der gleiche
 * Spieler-Name muss auf allen Geraeten und nach jedem App-Start die
 * gleiche Farbe ergeben. Frueher loste die alte PlayerColors-Klasse
 * das ueber einen mutableMap, der bei jedem Spieler einen neuen
 * Eintrag erzeugte; die Farbe haengt damit von der Eintrag-Reihenfolge
 * ab -- zwei Geraete sehen unterschiedliche Farben, je nachdem in
 * welcher Reihenfolge die Spieler beim Renderer ankamen.
 *
 * Hier loesen wir das ueber den Hashcode des Namens. Ergebnis ist
 * deterministisch und seiteneffekt-frei.
 */
object PlayerColorMap {

    private val PALETTE: List<Int> = listOf(
        Color.RED,
        Color.BLUE,
        Color.GREEN,
        Color.YELLOW
    )

    /**
     * Liefert die ARGB-Farbe fuer einen Spieler-Namen.
     *
     * Der Modulo-Trick stellt sicher, dass auch bei mehr als
     * [PALETTE].size Spielern eine gueltige Farbe geliefert wird --
     * praktisch ist die Spieleranzahl aber durch das Spiel auf
     * <= 4 begrenzt, sodass Kollisionen normalerweise nicht
     * vorkommen.
     */
    fun colorFor(player: String): Int {
        val index = (player.hashCode() and Int.MAX_VALUE) % PALETTE.size
        return PALETTE[index]
    }
}
