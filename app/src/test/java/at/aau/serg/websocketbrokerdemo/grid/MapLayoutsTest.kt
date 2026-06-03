package at.aau.serg.websocketbrokerdemo.grid

import at.aau.serg.websocketbrokerdemo.ui.mainmenu.GameMode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Tests fuer MapLayouts.
 *
 * Pruefen: forMode-Mapping ist vollstaendig, die drei Layouts haben
 * sinnvoll skalierte Hex-Groessen (kleiner bei groesserem Brett).
 */
class MapLayoutsTest {

    @Test
    fun `forMode returns DUAL_VALLEY for DUAL_VALLEY`() {
        assertSame(MapLayouts.DUAL_VALLEY, MapLayouts.forMode(GameMode.DUAL_VALLEY))
    }

    @Test
    fun `forMode returns TRIAD_OUTPOST for TRIAD_OUTPOST`() {
        assertSame(MapLayouts.TRIAD_OUTPOST, MapLayouts.forMode(GameMode.TRIAD_OUTPOST))
    }

    @Test
    fun `forMode returns BATTLEFIELD_PEAKS for BATTLEFIELD_PEAKS`() {
        assertSame(MapLayouts.BATTLEFIELD_PEAKS, MapLayouts.forMode(GameMode.BATTLEFIELD_PEAKS))
    }

    @Test
    fun `layouts grow in size with player count`() {
        // Mehr Spieler -> groessere Brett-Flaeche.
        val dualArea = MapLayouts.DUAL_VALLEY.rows * MapLayouts.DUAL_VALLEY.cols
        val triadArea = MapLayouts.TRIAD_OUTPOST.rows * MapLayouts.TRIAD_OUTPOST.cols
        val battleArea = MapLayouts.BATTLEFIELD_PEAKS.rows * MapLayouts.BATTLEFIELD_PEAKS.cols

        assertTrue(triadArea > dualArea, "Triad muss groesser sein als Dual")
        assertTrue(battleArea > triadArea, "Battlefield muss groesser sein als Triad")
    }

    @Test
    fun `hex sizes shrink as boards grow`() {
        // Damit die Karte trotzdem auf den Bildschirm passt, werden
        // Hexes bei groesseren Karten kleiner.
        assertTrue(MapLayouts.TRIAD_OUTPOST.hexSize < MapLayouts.DUAL_VALLEY.hexSize)
        assertTrue(MapLayouts.BATTLEFIELD_PEAKS.hexSize < MapLayouts.TRIAD_OUTPOST.hexSize)
    }

    @Test
    fun `all layouts have unique names`() {
        val names = listOf(
            MapLayouts.DUAL_VALLEY.name,
            MapLayouts.TRIAD_OUTPOST.name,
            MapLayouts.BATTLEFIELD_PEAKS.name
        )
        assertEquals(names.size, names.toSet().size)
    }

    @Test
    fun `all layouts have positive dimensions`() {
        listOf(
            MapLayouts.DUAL_VALLEY,
            MapLayouts.TRIAD_OUTPOST,
            MapLayouts.BATTLEFIELD_PEAKS
        ).forEach { layout ->
            assertTrue(layout.rows > 0)
            assertTrue(layout.cols > 0)
            assertTrue(layout.hexSize > 0f)
        }
    }
}
