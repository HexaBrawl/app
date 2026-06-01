package at.aau.serg.websocketbrokerdemo.ui.mainmenu

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Reine Daten-Tests für den GameMode-Enum.
 *
 * Hier prüfen wir nur die statische Konfiguration: Routen-Namen,
 * Spieleranzahlen, eindeutige Resource-IDs und dass valueOf funktioniert.
 *
 * Die fromRoute-Lookup-Logik ist seit dem Refactoring in GameModeLogic
 * ausgelagert -- die wird in GameModeLogicTest abgedeckt.
 *
 * Resource-IDs (R.string.*, R.drawable.*) werden NICHT auf konkrete Werte
 * geprüft, weil die im Unit-Test-Classpath nicht generiert werden --
 * stattdessen verifizieren wir nur, dass sie überhaupt unterschiedlich
 * pro Modus sind (Konfliktschutz beim Erweitern).
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