package at.aau.serg.websocketbrokerdemo.ui.lobby_modes

import at.aau.serg.websocketbrokerdemo.ui.mainmenu.GameMode
import at.aau.serg.websocketbrokerdemo.ui.navigation.Screen
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Tests fuer LobbyLogic.
 *
 * Reines Mapping-Test: pro GameMode der passende Wartelobby-Screen.
 * Wenn ein neuer Modus dazukommt, muss hier ein neuer Eintrag her,
 * sonst kompiliert das when in LobbyLogic nicht mehr (sealed sub-
 * types in Screen sind genau drei Stueck).
 */
class LobbyLogicTest {

    @Test
    fun `DUAL_VALLEY maps to WaitingDual`() {
        assertEquals(Screen.WaitingDual, LobbyLogic.toWaitingScreen(GameMode.DUAL_VALLEY))
    }

    @Test
    fun `TRIAD_OUTPOST maps to WaitingTriad`() {
        assertEquals(Screen.WaitingTriad, LobbyLogic.toWaitingScreen(GameMode.TRIAD_OUTPOST))
    }

    @Test
    fun `BATTLEFIELD_PEAKS maps to WaitingBattlefield`() {
        assertEquals(Screen.WaitingBattlefield, LobbyLogic.toWaitingScreen(GameMode.BATTLEFIELD_PEAKS))
    }
}
