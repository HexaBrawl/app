package at.aau.serg.websocketbrokerdemo.ui.game

import at.aau.serg.websocketbrokerdemo.data.serverside.GameStatus
import at.aau.serg.websocketbrokerdemo.data.serverside.Player
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GameOverLogicTest {

    private val alice = "Alice"
    private val bob = "Bob"
    private fun player(name: String) = Player(name = name)

    // ---- isGameOver ----------------------------------------------------

    @Test
    fun `isGameOver true when status FINISHED`() {
        val players = listOf(player(alice), player(bob))
        assertTrue(GameOverLogic.isGameOver(players, GameStatus.FINISHED, alice))
    }

    @Test
    fun `isGameOver true when local player no longer in list (eliminated)`() {
        val players = listOf(player(bob)) // Alice ausgeschieden
        assertTrue(GameOverLogic.isGameOver(players, GameStatus.IN_PROGRESS, alice))
    }

    @Test
    fun `isGameOver false when in progress and local player present`() {
        val players = listOf(player(alice), player(bob))
        assertFalse(GameOverLogic.isGameOver(players, GameStatus.IN_PROGRESS, alice))
    }

    // ---- isLocalWinner -------------------------------------------------

    @Test
    fun `isLocalWinner true when winner equals local name`() {
        assertTrue(GameOverLogic.isLocalWinner(alice, alice))
    }

    @Test
    fun `isLocalWinner false for other winner or null`() {
        assertFalse(GameOverLogic.isLocalWinner(bob, alice))
        assertFalse(GameOverLogic.isLocalWinner(null, alice))
    }
}
