package at.aau.serg.websocketbrokerdemo.ui.mainmenu

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Reine Daten-Tests fuer den GameMode-Enum.
 *
 * Wir pruefen die statische Konfiguration des Enums (Spieleranzahlen,
 * Resource-Eindeutigkeit) und dass valueOf funktioniert. Routen sind
 * nicht mehr im Enum -- das Mapping GameMode -> Lobby-Screen lebt in
 * [MainMenuLogic.screenForMode] und wird dort getestet, das Mapping
 * GameMode -> Wartelobby-Screen in
 * [at.aau.serg.websocketbrokerdemo.ui.lobby_modes.LobbyLogic.toWaitingScreen].
 *
 * Resource-IDs (R.string.*, R.drawable.*) werden NICHT auf konkrete
 * Werte geprueft, weil die im Unit-Test-Classpath nicht generiert
 * werden -- stattdessen verifizieren wir nur, dass sie ueberhaupt
 * unterschiedlich pro Modus sind (Konfliktschutz beim Erweitern).
 */
class GameModeTest {

    @Test
    fun `there are exactly three game modes`() {
        assertEquals(3, GameMode.entries.size)
    }

    @Test
    fun `player counts are 2 3 and 4`() {
        assertEquals(2, GameMode.DUAL_VALLEY.playerCount)
        assertEquals(3, GameMode.TRIAD_OUTPOST.playerCount)
        assertEquals(4, GameMode.BATTLEFIELD_PEAKS.playerCount)
    }

    @Test
    fun `each mode references string and drawable resources`() {
        val nameIds = GameMode.entries.map { it.nameRes }.toSet()
        val taglineIds = GameMode.entries.map { it.taglineRes }.toSet()
        val drawableIds = GameMode.entries.map { it.backgroundRes }.toSet()

        assertEquals(3, nameIds.size, "Each mode should have a unique name resource")
        assertEquals(3, taglineIds.size, "Each mode should have a unique tagline resource")
        assertEquals(3, drawableIds.size, "Each mode should have a unique drawable resource")
    }

    @Test
    fun `mode count matches max player count`() {
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
