package at.aau.serg.websocketbrokerdemo.ui.lobby_modes

import at.aau.serg.websocketbrokerdemo.ui.mainmenu.GameMode
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class LobbyLogicTest {

    @Test
    fun `toWaitingRoute returns correct route for each mode`() {
        Assertions.assertEquals("waiting_dual", LobbyLogic.toWaitingRoute(GameMode.DUAL_VALLEY))
        Assertions.assertEquals("waiting_triad", LobbyLogic.toWaitingRoute(GameMode.TRIAD_OUTPOST))
        Assertions.assertEquals(
            "waiting_battlefield",
            LobbyLogic.toWaitingRoute(GameMode.BATTLEFIELD_PEAKS)
        )
    }
}