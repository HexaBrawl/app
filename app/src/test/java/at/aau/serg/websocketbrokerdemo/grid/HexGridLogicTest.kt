package at.aau.serg.websocketbrokerdemo.grid

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.math.abs

/**
 * Tests fuer HexGridLogic.
 *
 * Geometrische Pure-Functions, deshalb gut testbar. Wir nutzen ein
 * kleines testbares Layout (4x4, hexSize 10f) damit die erwarteten
 * Pixel-Koordinaten ueberschaubar bleiben.
 */
class HexGridLogicTest {

    private val testLayout = MapLayout(rows = 4, cols = 4, hexSize = 10f, name = "test")

    // ---- cellCenter ---------------------------------------------------

    @Test
    fun `cellCenter is centered around origin`() {
        val (x0, y0) = HexGridLogic.cellCenter(0, 0, testLayout)
        val (x3, y3) = HexGridLogic.cellCenter(3, 3, testLayout)

        assertTrue(x0 < 0f, "Zelle (0,0) muss links der Mitte liegen")
        assertTrue(x3 > 0f, "Zelle (3,3) muss rechts der Mitte liegen")
        assertTrue(abs(x0 + x3) < 0.001f, "Grid muss symmetrisch sein")
    }

    @Test
    fun `cellCenter offsets odd columns vertically`() {
        val (_, evenY) = HexGridLogic.cellCenter(0, 0, testLayout)
        val (_, oddY) = HexGridLogic.cellCenter(1, 0, testLayout)
        assertTrue(oddY > evenY, "Ungerade Spalten muessen tiefer liegen")
    }

    @Test
    fun `cellCenter 1x1 Grid liegt bei Ursprung`() {
        val layout = MapLayout(cols = 1, rows = 1, hexSize = 30f, name = "single")
        val (x, y) = HexGridLogic.cellCenter(0, 0, layout)
        assertTrue(abs(x) < 0.01f, "x sollte 0 sein")
        assertTrue(abs(y) < 0.01f, "y sollte 0 sein")
    }

    @Test
    fun `cellCenter horizontaler Abstand zwischen Spalten ist gleich`() {
        val layout = MapLayout(cols = 3, rows = 1, hexSize = 50f, name = "row")
        val (x0, _) = HexGridLogic.cellCenter(0, 0, layout)
        val (x1, _) = HexGridLogic.cellCenter(1, 0, layout)
        val (x2, _) = HexGridLogic.cellCenter(2, 0, layout)
        assertTrue(abs((x1 - x0) - (x2 - x1)) < 0.01f, "Abstand zwischen Spalten muss gleich sein")
    }

    // ---- pixelToCell --------------------------------------------------

    @Test
    fun `pixelToCell returns the cell at its center`() {
        val (cx, cy) = HexGridLogic.cellCenter(2, 1, testLayout)
        val result = HexGridLogic.pixelToCell(cx, cy, testLayout)
        assertEquals(2 to 1, result)
    }

    @Test
    fun `pixelToCell returns null far outside the grid`() {
        val result = HexGridLogic.pixelToCell(10000f, 10000f, testLayout)
        assertNull(result)
    }

    @Test
    fun `pixelToCell hits the nearest cell for offset positions`() {
        val (cx, cy) = HexGridLogic.cellCenter(1, 1, testLayout)
        val result = HexGridLogic.pixelToCell(cx + 2f, cy + 2f, testLayout)
        assertEquals(1 to 1, result)
    }

    @Test
    fun `pixelToCell trifft Zelle (0,0) genau am Mittelpunkt`() {
        val layout = MapLayout(cols = 5, rows = 5, hexSize = 40f, name = "big")
        val (cx, cy) = HexGridLogic.cellCenter(0, 0, layout)
        val result = HexGridLogic.pixelToCell(cx, cy, layout)
        assertEquals(0 to 0, result)
    }

    @Test
    fun `pixelToCell knapp innerhalb des Radius trifft noch die Zelle`() {
        val (cx, cy) = HexGridLogic.cellCenter(0, 0, testLayout)
        // hexSize = 10f, also 9f Abstand sollte noch treffen
        val result = HexGridLogic.pixelToCell(cx + 9f, cy, testLayout)
        assertEquals(0 to 0, result)
    }

    // ---- hexCorners ---------------------------------------------------

    @Test
    fun `hexCorners returns exactly 6 points`() {
        val corners = HexGridLogic.hexCorners(0f, 0f, 10f)
        assertEquals(6, corners.size)
    }

    @Test
    fun `hexCorners are at distance size from center`() {
        val corners = HexGridLogic.hexCorners(0f, 0f, 10f)
        corners.forEach { (x, y) ->
            val dist = kotlin.math.sqrt(x * x + y * y)
            assertTrue(abs(dist - 10f) < 0.01f, "Ecke muss auf Hex-Radius liegen")
        }
    }

    @Test
    fun `hexCorners Mittelpunkt der Ecken ist der Ursprung`() {
        val corners = HexGridLogic.hexCorners(100f, 200f, 40f)
        val avgX = corners.map { it.first }.average().toFloat()
        val avgY = corners.map { it.second }.average().toFloat()
        assertTrue(abs(avgX - 100f) < 0.1f)
        assertTrue(abs(avgY - 200f) < 0.1f)
    }

    @Test
    fun `hexCorners skalieren proportional mit der Groesse`() {
        val corners1 = HexGridLogic.hexCorners(0f, 0f, 50f)
        val corners2 = HexGridLogic.hexCorners(0f, 0f, 100f)
        assertTrue(abs(corners2[0].first - corners1[0].first * 2f) < 0.1f)
    }

    // ---- unitRadius ---------------------------------------------------

    @Test
    fun `unitRadius is smaller than hex size`() {
        assertTrue(HexGridLogic.unitRadius(10f) < 10f)
    }

    @Test
    fun `unitRadius scales linearly with hex size`() {
        val small = HexGridLogic.unitRadius(10f)
        val big = HexGridLogic.unitRadius(20f)
        assertEquals(small * 2f, big, 0.0001f)
    }

    @Test
    fun `unitRadius fuer hexSize 50 ist 20`() {
        assertTrue(abs(HexGridLogic.unitRadius(50f) - 20f) < 0.01f)
    }

    // ---- allCells -----------------------------------------------------

    @Test
    fun `allCells yields rows times cols entries`() {
        val cells = HexGridLogic.allCells(testLayout).toList()
        assertEquals(testLayout.rows * testLayout.cols, cells.size)
    }

    @Test
    fun `allCells covers each cell exactly once`() {
        val cells = HexGridLogic.allCells(testLayout).toSet()
        assertEquals(testLayout.rows * testLayout.cols, cells.size)
    }

    @Test
    fun `allCells respects layout bounds`() {
        val cells = HexGridLogic.allCells(testLayout).toList()
        cells.forEach { (col, row) ->
            assertTrue(col in 0 until testLayout.cols)
            assertTrue(row in 0 until testLayout.rows)
        }
    }

    @Test
    fun `allCells fuer 1x1 Grid liefert genau eine Zelle`() {
        val layout = MapLayout(cols = 1, rows = 1, hexSize = 30f, name = "single")
        val cells = HexGridLogic.allCells(layout).toList()
        assertEquals(1, cells.size)
        assertEquals(0 to 0, cells[0])
    }

    @Test
    fun `allCells enthaelt alle Kombinationen bei 2x2 Grid`() {
        val layout = MapLayout(cols = 2, rows = 2, hexSize = 30f, name = "small")
        val cells = HexGridLogic.allCells(layout).toList()
        assertTrue(cells.contains(0 to 0))
        assertTrue(cells.contains(0 to 1))
        assertTrue(cells.contains(1 to 0))
        assertTrue(cells.contains(1 to 1))
    }
}
