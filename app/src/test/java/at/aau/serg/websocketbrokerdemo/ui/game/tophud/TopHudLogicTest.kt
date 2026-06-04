package at.aau.serg.websocketbrokerdemo.ui.game.tophud

import at.aau.serg.websocketbrokerdemo.data.serverside.Player
import at.aau.serg.websocketbrokerdemo.data.serverside.PlayerColor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Tests fuer TopHudLogic. Pure-Function-Tests.
 */
class TopHudLogicTest {

    private val alice = Player(name = "Alice", color = PlayerColor.RED, gold = 3250, income = 120)
    private val bob = Player(name = "Bob", color = PlayerColor.BLUE, gold = 800, income = 50)

    @Test
    fun `goldFor returns gold of local player`() {
        assertEquals(3250, TopHudLogic.goldFor(listOf(alice, bob), "Alice"))
    }

    @Test
    fun `goldFor returns 0 when local player is unknown`() {
        assertEquals(0, TopHudLogic.goldFor(listOf(alice, bob), "Ghost"))
    }

    @Test
    fun `goldFor returns 0 when local name is null`() {
        assertEquals(0, TopHudLogic.goldFor(listOf(alice, bob), null))
    }

    @Test
    fun `goldFor returns 0 for empty players list`() {
        assertEquals(0, TopHudLogic.goldFor(emptyList(), "Alice"))
    }

    @Test
    fun `incomeFor returns income of local player`() {
        assertEquals(120, TopHudLogic.incomeFor(listOf(alice, bob), "Alice"))
    }

    @Test
    fun `incomeFor returns 0 when local player is unknown`() {
        assertEquals(0, TopHudLogic.incomeFor(listOf(alice, bob), "Ghost"))
    }

    @Test
    fun `incomeFor returns 0 when local name is null`() {
        assertEquals(0, TopHudLogic.incomeFor(listOf(alice, bob), null))
    }

    @Test
    fun `incomeFor returns 0 for empty players list`() {
        assertEquals(0, TopHudLogic.incomeFor(emptyList(), "Alice"))
    }
}
