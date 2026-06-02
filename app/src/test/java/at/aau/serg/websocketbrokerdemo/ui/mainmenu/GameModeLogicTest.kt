package at.aau.serg.websocketbrokerdemo.ui.mainmenu

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

/**
 * Tests fuer GameModeLogic.
 *
 * Aktuell nur byPlayerCount; das fromRoute-Lookup ist seit Phase 1b
 * entfernt, weil GameMode keine route-Property mehr besitzt. Routen
 * werden ueber [at.aau.serg.websocketbrokerdemo.ui.navigation.Screen]
 * adressiert.
 */
class GameModeLogicTest {

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
        assertNull(GameModeLogic.byPlayerCount(1))
        assertNull(GameModeLogic.byPlayerCount(5))
        assertNull(GameModeLogic.byPlayerCount(0))
        assertNull(GameModeLogic.byPlayerCount(-1))
    }
}
