package at.aau.serg.websocketbrokerdemo.ui.lobby_modes

import androidx.compose.ui.graphics.Color
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Tests fuer das SealColor-Enum.
 *
 * Verifiziert dass die drei Siegel-Varianten existieren und jeweils
 * paarig (main + dark) korrekt definiert sind. Die konkreten Farb-Werte
 * pruefen wir nicht auf den exakten Hex-Code -- das waere ein
 * Tautologie-Test gegen die Implementierung. Stattdessen pruefen wir
 * strukturelle Invarianten: jede Variante hat zwei unterschiedliche
 * Farben, alle drei Varianten sind voneinander unterscheidbar, und
 * valueOf liefert die richtige Instanz.
 */
class SealColorTest {

    @Test
    fun `there are exactly three seal colors`() {
        // Wenn eine vierte Variante dazukommt (z. B. Green/Silver) sollte
        // dieser Test brechen und uns daran erinnern, die LobbyScreen-
        // Wachs-Siegel-Zuordnung mit zu pflegen.
        assertEquals(3, SealColor.entries.size)
    }

    @Test
    fun `Red Blue and Gold are all defined`() {
        // Konkret pruefen welche Varianten existieren, damit ein
        // versehentliches Umbenennen sofort auffaellt.
        val names = SealColor.entries.map { it.name }.toSet()
        assertTrue("Red" in names)
        assertTrue("Blue" in names)
        assertTrue("Gold" in names)
    }

    @Test
    fun `each seal has distinct main and dark colors`() {
        // Eine Siegel-Variante muss zwei verschiedene Toene haben --
        // sonst gibt's keinen radialen Verlauf, das Siegel wirkt platt.
        SealColor.entries.forEach { seal ->
            assertNotEquals(
                seal.main, seal.dark,
                "${seal.name}: main and dark must differ for radial gradient"
            )
        }
    }

    @Test
    fun `main colors are unique across seals`() {
        // Damit der User die drei Aktionen optisch unterscheiden kann,
        // muss jede Variante einen eigenen main-Ton haben.
        val mains = SealColor.entries.map { it.main }
        assertEquals(mains.size, mains.toSet().size, "Main colors must be unique per seal")
    }

    @Test
    fun `dark colors are unique across seals`() {
        // Symmetrisch: auch die Schatten-Toene sollen sich unterscheiden.
        val darks = SealColor.entries.map { it.dark }
        assertEquals(darks.size, darks.toSet().size, "Dark colors must be unique per seal")
    }

    @Test
    fun `valueOf works for all defined seals`() {
        // Sanity-Check, fuer den Fall dass SealColor irgendwann per
        // String-Lookup aus einem Repository geladen wird.
        assertEquals(SealColor.Red, SealColor.valueOf("Red"))
        assertEquals(SealColor.Blue, SealColor.valueOf("Blue"))
        assertEquals(SealColor.Gold, SealColor.valueOf("Gold"))
    }

    @Test
    fun `main and dark colors are non-transparent`() {
        // Wenn eine Farbe versehentlich Color.Transparent waere, wuerde
        // das Siegel unsichtbar werden. Wir pruefen Alpha != 0.
        SealColor.entries.forEach { seal ->
            assertTrue(
                seal.main != Color.Transparent,
                "${seal.name}.main must not be transparent"
            )
            assertTrue(
                seal.dark != Color.Transparent,
                "${seal.name}.dark must not be transparent"
            )
        }
    }
}