package at.aau.serg.websocketbrokerdemo.ui.lobby_modes

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Tests fuer LobbyState.
 *
 * Defaults und die computed `canJoin`-Property. Die zugrundeliegende
 * Validierungs-Logik (JoinByCodeLogic.isValid) ist separat in
 * JoinByCodeLogicTest abgedeckt; hier nur sanity-checks dass der
 * State sie richtig verkabelt.
 */
class LobbyStateTest {

    @Test
    fun `default state has dialog closed and empty code`() {
        val state = LobbyState()
        assertFalse(state.showJoinDialog)
        assertEquals("", state.code)
    }

    @Test
    fun `canJoin is false for empty code`() {
        val state = LobbyState(code = "")
        assertFalse(state.canJoin)
    }

    @Test
    fun `canJoin is false for short code`() {
        // Min is now 6
        val state = LobbyState(code = "ABCDE")
        assertFalse(state.canJoin)
    }

    @Test
    fun `canJoin is true for valid 6-character code`() {
        val state = LobbyState(code = "123456")
        assertTrue(state.canJoin)
    }

    @Test
    fun `canJoin is false for long code`() {
        // Max is now 6
        val state = LobbyState(code = "1234567")
        assertFalse(state.canJoin)
    }
}
