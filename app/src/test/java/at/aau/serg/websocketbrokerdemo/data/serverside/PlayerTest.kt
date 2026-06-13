package at.aau.serg.websocketbrokerdemo.data.serverside

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PlayerTest {

    @Test
    fun `Player default values are correct`() {
        val player = Player()
        assertEquals("", player.name)
        assertEquals("", player.sessionId)
        assertEquals(PlayerColor.RED, player.color)
    }

    @Test
    fun `Player custom values are correctly set`() {
        val player = Player("Alice", "session-123", PlayerColor.BLUE)
        assertEquals("Alice", player.name)
        assertEquals("session-123", player.sessionId)
        assertEquals(PlayerColor.BLUE, player.color)
    }

    @Test
    fun `Player connected defaults to true`() {
        // Default true ist wichtig: alte GameState-Broadcasts vom Server,
        // die noch nicht das connected-Feld serialisieren, sollen den
        // Spieler nicht faelschlich als disconnected zeigen.
        assertTrue(Player().connected)
    }

    @Test
    fun `Player connected can be set to false`() {
        val player = Player(name = "Bob", connected = false)
        assertFalse(player.connected)
    }
}
