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
}
