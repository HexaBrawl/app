package at.aau.serg.websocketbrokerdemo.ui.waiting

import at.aau.serg.websocketbrokerdemo.ui.waiting.model.PlayerSlot
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Tests fuer WaitingLobbyState.
 *
 * Minimal -- der State ist eine reine Datenklasse mit einer computed
 * property (isCountdownActive). Hier nur Sanity-Checks dafuer.
 */
class WaitingLobbyStateTest {

    @Test
    fun `default state has no slots and inactive countdown`() {
        val state = WaitingLobbyState()
        assertEquals(emptyList<PlayerSlot>(), state.slots)
        assertEquals(-1, state.countdown)
        assertFalse(state.isCountdownActive)
    }

    @Test
    fun `isCountdownActive is true when countdown is positive`() {
        val state = WaitingLobbyState(countdown = 3)
        assertTrue(state.isCountdownActive)
    }

    @Test
    fun `isCountdownActive is false when countdown is zero`() {
        val state = WaitingLobbyState(countdown = 0)
        assertFalse(state.isCountdownActive)
    }

    @Test
    fun `isCountdownActive is false when countdown is negative`() {
        val state = WaitingLobbyState(countdown = -1)
        assertFalse(state.isCountdownActive)
    }

    @Test
    fun `default state has countdownComplete false`() {
        val state = WaitingLobbyState()
        assertFalse(state.countdownComplete)
    }

    @Test
    fun `default state has no errorMessage`() {
        val state = WaitingLobbyState()
        assertNull(state.errorMessage)
    }
}
