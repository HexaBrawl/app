package at.aau.serg.websocketbrokerdemo.ui.waiting

import at.aau.serg.websocketbrokerdemo.data.serverside.ErrorCode
import at.aau.serg.websocketbrokerdemo.data.serverside.GameStatus
import at.aau.serg.websocketbrokerdemo.data.serverside.Player
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LobbyNetworkLogicTest {

    private val alice = "Alice"
    private val bob = "Bob"
    private fun player(name: String) = Player(name = name)

    // ---- shouldJoin ----------------------------------------------------

    @Test
    fun `shouldJoin true when ready, name and room present`() {
        assertTrue(LobbyNetworkLogic.shouldJoin(localReady = true, localName = alice, roomId = "room1"))
    }

    @Test
    fun `shouldJoin false when not ready`() {
        assertFalse(LobbyNetworkLogic.shouldJoin(localReady = false, localName = alice, roomId = "room1"))
    }

    @Test
    fun `shouldJoin false when name blank`() {
        assertFalse(LobbyNetworkLogic.shouldJoin(localReady = true, localName = "  ", roomId = "room1"))
    }

    @Test
    fun `shouldJoin false when roomId blank`() {
        assertFalse(LobbyNetworkLogic.shouldJoin(localReady = true, localName = alice, roomId = ""))
    }

    // ---- isLocalPlayerPresent / remotePlayers --------------------------

    @Test
    fun `isLocalPlayerPresent reflects membership by name`() {
        val players = listOf(player(alice), player(bob))
        assertTrue(LobbyNetworkLogic.isLocalPlayerPresent(players, alice))
        assertFalse(LobbyNetworkLogic.isLocalPlayerPresent(players, "Carol"))
    }

    @Test
    fun `remotePlayers excludes the local player`() {
        val players = listOf(player(alice), player(bob))
        val remotes = LobbyNetworkLogic.remotePlayers(players, alice)
        assertEquals(listOf(bob), remotes.map { it.name })
    }

    // ---- shouldNavigateToGame ------------------------------------------

    @Test
    fun `shouldNavigateToGame true only when countdown done and in progress`() {
        assertTrue(LobbyNetworkLogic.shouldNavigateToGame(true, GameStatus.IN_PROGRESS))
    }

    @Test
    fun `shouldNavigateToGame false when countdown not complete`() {
        assertFalse(LobbyNetworkLogic.shouldNavigateToGame(false, GameStatus.IN_PROGRESS))
    }

    @Test
    fun `shouldNavigateToGame false when not in progress or status null`() {
        assertFalse(LobbyNetworkLogic.shouldNavigateToGame(true, GameStatus.WAITING_FOR_PLAYERS))
        assertFalse(LobbyNetworkLogic.shouldNavigateToGame(true, null))
    }

    // ---- requiresReselection -------------------------------------------

    @Test
    fun `requiresReselection true for color and name conflicts`() {
        assertTrue(LobbyNetworkLogic.requiresReselection(ErrorCode.COLOR_ALREADY_TAKEN))
        assertTrue(LobbyNetworkLogic.requiresReselection(ErrorCode.NAME_ALREADY_TAKEN))
    }

    @Test
    fun `requiresReselection false for other or null errors`() {
        assertFalse(LobbyNetworkLogic.requiresReselection(ErrorCode.GAME_FULL))
        assertFalse(LobbyNetworkLogic.requiresReselection(null))
    }
}
