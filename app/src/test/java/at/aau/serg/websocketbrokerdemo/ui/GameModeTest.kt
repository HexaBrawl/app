package at.aau.serg.websocketbrokerdemo.ui.mainmenu

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Reine Daten-Tests für GameMode.
 *
 * Wir prüfen die statische Konfiguration des Enums (Routes, Spieleranzahlen)
 * und die fromRoute-Lookup-Logik. Resource-IDs (R.string.*, R.drawable.*)
 * werden NICHT auf konkrete Werte geprüft, weil die im Test-Classpath nicht
 * generiert werden – stattdessen verifizieren wir nur, dass sie überhaupt
 * gesetzt wurden (Wert != 0 wäre üblich, aber im Unit-Test ist R nicht
 * verfügbar; wir verlassen uns auf Kompilierfähigkeit).
 */
class GameModeTest {

    @Test
    fun `there are exactly three game modes`() {
        assertEquals(3, GameMode.entries.size)
    }

    @Test
    fun `each mode has a unique route`() {
        val routes = GameMode.entries.map { it.route }
        assertEquals(routes.size, routes.toSet().size, "Routes must be unique")
    }

    @Test
    fun `player counts are 2 3 and 4`() {
        assertEquals(2, GameMode.DUAL_VALLEY.playerCount)
        assertEquals(3, GameMode.TRIAD_OUTPOST.playerCount)
        assertEquals(4, GameMode.BATTLEFIELD_PEAKS.playerCount)
    }

    @Test
    fun `routes match expected naming`() {
        assertEquals("lobby_dual", GameMode.DUAL_VALLEY.route)
        assertEquals("lobby_triad", GameMode.TRIAD_OUTPOST.route)
        assertEquals("lobby_battlefield", GameMode.BATTLEFIELD_PEAKS.route)
    }

    @Test
    fun `fromRoute returns matching mode`() {
        assertEquals(GameMode.DUAL_VALLEY, GameMode.fromRoute("lobby_dual"))
        assertEquals(GameMode.TRIAD_OUTPOST, GameMode.fromRoute("lobby_triad"))
        assertEquals(GameMode.BATTLEFIELD_PEAKS, GameMode.fromRoute("lobby_battlefield"))
    }

    @Test
    fun `fromRoute returns null for unknown route`() {
        assertNull(GameMode.fromRoute("lobby_unknown"))
        assertNull(GameMode.fromRoute(""))
    }

    @Test
    fun `fromRoute handles null input`() {
        assertNull(GameMode.fromRoute(null))
    }

    @Test
    fun `each mode references string and drawable resources`() {
        // Resource-IDs sind Ints; der genaue Wert ist nicht relevant,
        // aber sie sollten unterschiedlich pro Modus sein.
        val nameIds = GameMode.entries.map { it.nameRes }.toSet()
        val taglineIds = GameMode.entries.map { it.taglineRes }.toSet()
        val drawableIds = GameMode.entries.map { it.backgroundRes }.toSet()

        assertEquals(3, nameIds.size, "Each mode should have a unique name resource")
        assertEquals(3, taglineIds.size, "Each mode should have a unique tagline resource")
        assertEquals(3, drawableIds.size, "Each mode should have a unique drawable resource")
    }

    @Test
    fun `mode count matches max player count`() {
        // Sanity check: dual=2, triad=3, battlefield=4
        val maxPlayers = GameMode.entries.maxOf { it.playerCount }
        val minPlayers = GameMode.entries.minOf { it.playerCount }
        assertEquals(4, maxPlayers)
        assertEquals(2, minPlayers)
    }

    @Test
    fun `valueOf works for canonical names`() {
        assertEquals(GameMode.DUAL_VALLEY, GameMode.valueOf("DUAL_VALLEY"))
        assertEquals(GameMode.TRIAD_OUTPOST, GameMode.valueOf("TRIAD_OUTPOST"))
        assertEquals(GameMode.BATTLEFIELD_PEAKS, GameMode.valueOf("BATTLEFIELD_PEAKS"))
    }
}
