package at.aau.serg.websocketbrokerdemo.ui.lobby_modes

import at.aau.serg.websocketbrokerdemo.ui.mainmenu.GameMode

object LobbyLogic {

    fun toWaitingRoute(mode: GameMode): String =
        when (mode) {
            GameMode.DUAL_VALLEY -> "waiting_dual"
            GameMode.TRIAD_OUTPOST -> "waiting_triad"
            GameMode.BATTLEFIELD_PEAKS -> "waiting_battlefield"
        }
}
