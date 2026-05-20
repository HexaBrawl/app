package at.aau.serg.websocketbrokerdemo.data.serverside

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GameStateTest {

    @Test
    fun `GameState default values are correct`() {
        val gameState = GameState()
        assertTrue(gameState.players.isEmpty())
        assertTrue(gameState.units.isEmpty())
        assertNull(gameState.currentTurn)
        assertEquals(GameStatus.WAITING_FOR_PLAYERS, gameState.status)
    }

    @Test
    fun `GameState properties can be updated`() {
        val players = mutableListOf(Player(name = "Player1"))
        val units = mutableListOf(GameUnit(player = "Player1", type = UnitType.INFANTRY))
        val gameState = GameState(
            players = players,
            units = units,
            currentTurn = "Player1",
            status = GameStatus.IN_PROGRESS
        )

        assertEquals(players, gameState.players)
        assertEquals(units, gameState.units)
        assertEquals("Player1", gameState.currentTurn)
        assertEquals(GameStatus.IN_PROGRESS, gameState.status)
    }
}
