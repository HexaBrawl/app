package at.aau.serg.websocketbrokerdemo.grid

import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Die gesamte Hex-Mathematik in EINEM Object.
 *
 * Frueher war das auf vier Files verteilt (HexLayout, HexGeometry,
 * HexInput, UniversalGridLogic). Das machte simple Aktionen wie
 * "wo wurde getippt?" zu einer Wanderung durch mehrere Klassen.
 *
 * Hier liegt jetzt alles zusammen, was zur Berechnung der Hex-Geometrie
 * gehoert: Zell-Mittelpunkte, Pixel-zu-Zelle-Mapping, Eckpunkte fuers
 * Zeichnen. Pure Funktionen ohne Compose-Abhaengigkeit, voll testbar.
 *
 * Konvention: Spalten sind versetzt -- ungerade Spalten ("odd-q
 * offset") sind um eine halbe Hex-Hoehe nach unten verschoben.
 */
object HexGridLogic {

    private const val ROW_OFFSET_FACTOR = 0.75f
    private const val SQRT3 = 1.7320508f
    private const val ANGLE_STEP_DEG = 60.0

    /**
     * Berechnet den Mittelpunkt einer Hex-Zelle in Pixel-Koordinaten.
     *
     * Das Grid wird so positioniert, dass (0,0) in der Mitte der
     * Spielflaeche liegt -- der Caller (Canvas) zeichnet bei
     * `contentAlignment = Center`, daher muss das Grid um sich selbst
     * zentriert sein.
     */
    fun cellCenter(col: Int, row: Int, layout: MapLayout): Pair<Float, Float> {
        val hexWidth = layout.hexSize * 2f
        val hexHeight = SQRT3 * layout.hexSize

        val hSpacing = hexWidth * ROW_OFFSET_FACTOR
        val vSpacing = hexHeight

        val offsetX = -((layout.cols - 1) * hSpacing) / 2f
        val offsetY = -((layout.rows - 1) * vSpacing) / 2f

        val x = offsetX + col * hSpacing
        val y = offsetY + row * vSpacing + if (col % 2 == 1) vSpacing / 2f else 0f
        return x to y
    }

    /**
     * Welche Zelle wurde an Pixel-Position (px, py) getippt?
     *
     * @return (col, row) wenn ein Hex getroffen wurde, null sonst.
     */
    fun pixelToCell(px: Float, py: Float, layout: MapLayout): Pair<Int, Int>? {
        for (row in 0 until layout.rows) {
            for (col in 0 until layout.cols) {
                val (cx, cy) = cellCenter(col, row, layout)
                val dx = px - cx
                val dy = py - cy
                val dist = sqrt(dx * dx + dy * dy)
                if (dist < layout.hexSize) return col to row
            }
        }
        return null
    }

    /**
     * Liefert die 6 Eckpunkte eines Hex fuer den Canvas-Path.
     */
    fun hexCorners(cx: Float, cy: Float, size: Float): List<Pair<Float, Float>> =
        (0..5).map { i ->
            val angle = Math.toRadians(ANGLE_STEP_DEG * i)
            val x = cx + size * cos(angle).toFloat()
            val y = cy + size * sin(angle).toFloat()
            x to y
        }

    /**
     * Standardradius einer Einheit, abhaengig von der Hex-Groesse.
     * Etwas kleiner als der Hex selbst, damit Rand sichtbar bleibt.
     */
    fun unitRadius(size: Float): Float = size / 2.5f

    /**
     * Hex-Distanz zwischen zwei Zellen in offset-Koordinaten.
     *
     * Konvertiert intern in Cube-Koordinaten (odd-q, ungerade Spalten
     * sind nach unten verschoben — passend zu [cellCenter]). Liefert die
     * minimale Anzahl an Hex-Schritten zwischen den beiden Feldern.
     */
    fun hexDistance(col1: Int, row1: Int, col2: Int, row2: Int): Int {
        val (ax, ay, az) = oddQToCube(col1, row1)
        val (bx, by, bz) = oddQToCube(col2, row2)
        return (abs(ax - bx) + abs(ay - by) + abs(az - bz)) / 2
    }

    private fun oddQToCube(col: Int, row: Int): Triple<Int, Int, Int> {
        val x = col
        val z = row - (col - (col and 1)) / 2
        val y = -x - z
        return Triple(x, y, z)
    }

    /**
     * Iteriert alle Zellen einer Karte. Aktuell ein einfaches Rechteck;
     * spaeter koennen hier andere Formen (z. B. Trefoil) leicht
     * ergaenzt werden.
     */
    fun allCells(layout: MapLayout): Sequence<Pair<Int, Int>> = sequence {
        for (row in 0 until layout.rows) {
            for (col in 0 until layout.cols) {
                yield(col to row)
            }
        }
    }
}
