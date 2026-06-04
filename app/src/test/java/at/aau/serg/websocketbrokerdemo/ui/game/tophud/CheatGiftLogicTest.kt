package at.aau.serg.websocketbrokerdemo.ui.game.tophud

import at.aau.serg.websocketbrokerdemo.data.serverside.PendingGift
import at.aau.serg.websocketbrokerdemo.data.serverside.Player
import at.aau.serg.websocketbrokerdemo.data.serverside.PlayerColor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.random.Random

/**
 * Tests fuer CheatGiftLogic.
 */
class CheatGiftLogicTest {

    private val alice = Player(name = "Alice", color = PlayerColor.RED, hasUsedGift = false)
    private val bob = Player(name = "Bob", color = PlayerColor.BLUE, hasUsedGift = true)

    // ---- rollDelta ----------------------------------------------------

    @Test
    fun `rollDelta is always within -10 to +10`() {
        val rng = Random(42)
        repeat(1000) {
            val delta = CheatGiftLogic.rollDelta(rng)
            assertTrue(delta in CheatGiftLogic.MIN_DELTA..CheatGiftLogic.MAX_DELTA)
        }
    }

    @Test
    fun `rollDelta with seeded random is deterministic`() {
        val a = CheatGiftLogic.rollDelta(Random(123))
        val b = CheatGiftLogic.rollDelta(Random(123))
        assertEquals(a, b)
    }

    @Test
    fun `rollDelta hits both ends across many rolls`() {
        val rng = Random(1)
        val results = (1..10000).map { CheatGiftLogic.rollDelta(rng) }
        assertTrue(results.any { it < 0 }, "Sollte auch negative Werte ziehen")
        assertTrue(results.any { it > 0 }, "Sollte auch positive Werte ziehen")
    }

    // ---- canUseGift ---------------------------------------------------

    @Test
    fun `canUseGift is true when player has not used gift yet`() {
        assertTrue(CheatGiftLogic.canUseGift(listOf(alice, bob), "Alice"))
    }

    @Test
    fun `canUseGift is false when player has already used gift`() {
        assertFalse(CheatGiftLogic.canUseGift(listOf(alice, bob), "Bob"))
    }

    @Test
    fun `canUseGift is false for unknown player`() {
        assertFalse(CheatGiftLogic.canUseGift(listOf(alice, bob), "Ghost"))
    }

    @Test
    fun `canUseGift is false for null name`() {
        assertFalse(CheatGiftLogic.canUseGift(listOf(alice, bob), null))
    }

    // ---- shouldShowStealPopup -----------------------------------------

    @Test
    fun `shouldShowStealPopup is false when pendingGift is null`() {
        assertFalse(CheatGiftLogic.shouldShowStealPopup(null, "Alice"))
    }

    @Test
    fun `shouldShowStealPopup is false for the owner`() {
        val gift = PendingGift(ownerName = "Alice", delta = 7, pendingDecisions = 1)
        assertFalse(CheatGiftLogic.shouldShowStealPopup(gift, "Alice"))
    }

    @Test
    fun `shouldShowStealPopup is true for other players`() {
        val gift = PendingGift(ownerName = "Alice", delta = 7, pendingDecisions = 1)
        assertTrue(CheatGiftLogic.shouldShowStealPopup(gift, "Bob"))
    }

    @Test
    fun `shouldShowStealPopup is false when local name is null`() {
        val gift = PendingGift(ownerName = "Alice", delta = 7, pendingDecisions = 1)
        assertFalse(CheatGiftLogic.shouldShowStealPopup(gift, null))
    }

    // ---- shouldShowWaitingOverlay -------------------------------------

    @Test
    fun `shouldShowWaitingOverlay is false when pendingGift is null`() {
        assertFalse(CheatGiftLogic.shouldShowWaitingOverlay(null, "Alice"))
    }

    @Test
    fun `shouldShowWaitingOverlay is true for the owner`() {
        val gift = PendingGift(ownerName = "Alice", delta = 7, pendingDecisions = 1)
        assertTrue(CheatGiftLogic.shouldShowWaitingOverlay(gift, "Alice"))
    }

    @Test
    fun `shouldShowWaitingOverlay is false for other players`() {
        val gift = PendingGift(ownerName = "Alice", delta = 7, pendingDecisions = 1)
        assertFalse(CheatGiftLogic.shouldShowWaitingOverlay(gift, "Bob"))
    }

    // ---- Konstanten ---------------------------------------------------

    @Test
    fun `CLICKS_TO_TRIGGER is 5`() {
        assertEquals(5, CheatGiftLogic.CLICKS_TO_TRIGGER)
    }

    @Test
    fun `delta range is symmetric around zero`() {
        assertEquals(-CheatGiftLogic.MIN_DELTA, CheatGiftLogic.MAX_DELTA)
    }
}
