package at.aau.serg.websocketbrokerdemo.ui.mainmenu

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

/**
 * Tests für GameModeLogic.
 *
 * Reine Lookup-Tests -- jede Route + jede Spielerzahl wird getestet,
 * plus die negativen Pfade (null / unknown).
 */
class GameModeLogicTest {

    // ---- fromRoute ------------------------------------------------------

    @Test
    fun `fromRoute returns DUAL_VALLEY for lobby_dual`() {
        assertEquals(GameMode.DUAL_VALLEY, GameModeLogic.fromRoute("lobby_dual"))
    }

    @Test
    fun `fromRoute returns TRIAD_OUTPOST for lobby_triad`() {
        assertEquals(GameMode.TRIAD_OUTPOST, GameModeLogic.fromRoute("lobby_triad"))
    }

    @Test
    fun `fromRoute returns BATTLEFIELD_PEAKS for lobby_battlefield`() {
        assertEquals(GameMode.BATTLEFIELD_PEAKS, GameModeLogic.fromRoute("lobby_battlefield"))
    }

    @Test
    fun `fromRoute returns null for unknown route`() {
        // Routen wie "home", "settings" etc. matchen keinen Modus
        assertNull(GameModeLogic.fromRoute("home"))
        assertNull(GameModeLogic.fromRoute("settings"))
        assertNull(GameModeLogic.fromRoute("totally_made_up"))
    }

    @Test
    fun `fromRoute returns null for null input`() {
        // Defensiv: null darf nicht crashen (currentBackStackEntry liefert
        // gelegentlich null -- z. B. direkt nach App-Start)
        assertNull(GameModeLogic.fromRoute(null))
    }

    @Test
    fun `fromRoute returns null for empty string`() {
        assertNull(GameModeLogic.fromRoute(""))
    }

    // ---- byPlayerCount --------------------------------------------------

    @Test
    fun `byPlayerCount returns DUAL_VALLEY for 2 players`() {
        assertEquals(GameMode.DUAL_VALLEY, GameModeLogic.byPlayerCount(2))
    }

    @Test
    fun `byPlayerCount returns TRIAD_OUTPOST for 3 players`() {
        assertEquals(GameMode.TRIAD_OUTPOST, GameModeLogic.byPlayerCount(3))
    }

    @Test
    fun `byPlayerCount returns BATTLEFIELD_PEAKS for 4 players`() {
        assertEquals(GameMode.BATTLEFIELD_PEAKS, GameModeLogic.byPlayerCount(4))
    }

    @Test
    fun `byPlayerCount returns null for invalid counts`() {
        // Es gibt keinen Solo-, 5+-Spieler- oder negativen-Modus
        assertNull(GameModeLogic.byPlayerCount(1))
        assertNull(GameModeLogic.byPlayerCount(5))
        assertNull(GameModeLogic.byPlayerCount(0))
        assertNull(GameModeLogic.byPlayerCount(-1))
    }
}
