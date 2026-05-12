package at.aau.serg.websocketbrokerdemo.ui

import at.aau.serg.websocketbrokerdemo.ui.lobby_modes.LobbyLogic
import at.aau.serg.websocketbrokerdemo.ui.mainmenu.GameMode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LobbyLogicTest {

    @Test
    fun `toWaitingRoute returns correct route for each mode`() {
        assertEquals("waiting_dual", LobbyLogic.toWaitingRoute(GameMode.DUAL_VALLEY))
        assertEquals("waiting_triad", LobbyLogic.toWaitingRoute(GameMode.TRIAD_OUTPOST))
        assertEquals("waiting_battlefield", LobbyLogic.toWaitingRoute(GameMode.BATTLEFIELD_PEAKS))
    }
}
