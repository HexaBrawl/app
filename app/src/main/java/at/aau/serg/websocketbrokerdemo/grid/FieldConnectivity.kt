package at.aau.serg.websocketbrokerdemo.grid

import at.aau.serg.websocketbrokerdemo.data.serverside.Field
import at.aau.serg.websocketbrokerdemo.data.serverside.GameUnit
import at.aau.serg.websocketbrokerdemo.data.serverside.UnitType

/**
 * Hex-Konnektivitaets-Berechnung im Frontend (subissue #172).
 *
 * Dupliziert bewusst die Server-Logik (GameService.recomputeConnectivity)
 * fuer die rein visuelle Frage: "welche Felder eines Spielers sind noch
 * per Hex-Nachbarschaft mit seiner Basis verbunden?".
 *
 * Warum nicht nur Field.isSkeleton vertrauen?
 * Der Server-Flag wird in Reset-Szenarien nicht zuverlaessig gesetzt
 * (z.B. wenn eine Bruecke einmal weggebrochen und wieder gebaut wurde).
 * Der Frontend rechnet die topologische Verbindung selbst aus und
 * rendert konsistent.
 *
 * Pure Funktion, seiteneffekt-frei, ohne Compose-Runtime testbar.
 */
object FieldConnectivity {

    /**
     * 6 Hex-Nachbarn in "odd-q offset"-Koordinaten.
     * Muss mit Server-Variante `GameService.hexNeighbors` synchron sein.
     */
    fun hexNeighbors(x: Int, y: Int): List<Pair<Int, Int>> =
        if (x % 2 == 0)
            listOf(x - 1 to y - 1, x - 1 to y, x to y - 1, x to y + 1, x + 1 to y - 1, x + 1 to y)
        else
            listOf(x - 1 to y, x - 1 to y + 1, x to y - 1, x to y + 1, x + 1 to y, x + 1 to y + 1)

    /**
     * Liefert alle Feld-Koordinaten (x,y) des Spielers, die per
     * Hex-Nachbarschaft mit seiner Basis verbunden sind.
     *
     * - Startpunkt: UnitType.BASE-Position des Spielers
     * - Pfade laufen ausschliesslich ueber Felder mit gleichem Owner
     * - Field.isSkeleton wird ignoriert -- es geht nur um Topologie
     *
     * @return leeres Set, wenn der Spieler keine BASE-Einheit hat oder
     *         die BASE auf keinem eigenen Feld steht.
     */
    fun connectedFields(
        fields: List<Field>,
        units: List<GameUnit>,
        playerName: String
    ): Set<Pair<Int, Int>> {
        val basePos = units.firstOrNull {
            it.player == playerName && it.type == UnitType.BASE
        }?.let { it.x to it.y } ?: return emptySet()

        val ownerCells = fields
            .filter { it.owner == playerName }
            .map { it.x to it.y }
            .toSet()

        if (basePos !in ownerCells) return emptySet()

        val visited = mutableSetOf(basePos)
        val queue = ArrayDeque<Pair<Int, Int>>()
        queue.add(basePos)

        while (queue.isNotEmpty()) {
            val (x, y) = queue.removeFirst()
            for ((nx, ny) in hexNeighbors(x, y)) {
                if ((nx to ny) in visited) continue
                if ((nx to ny) !in ownerCells) continue
                visited.add(nx to ny)
                queue.add(nx to ny)
            }
        }
        return visited
    }
}