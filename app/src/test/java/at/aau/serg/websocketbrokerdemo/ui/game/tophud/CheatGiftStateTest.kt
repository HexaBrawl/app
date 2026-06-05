package at.aau.serg.websocketbrokerdemo.ui.game.tophud

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

/**
 * Tests fuer CheatGiftState. Defaults + copy().
 */
class CheatGiftStateTest {

    @Test
    fun `default state has clickCount 0 and hasResponded false`() {
        val state = CheatGiftState()
        assertEquals(0, state.clickCount)
        assertFalse(state.hasResponded)
    }

    @Test
    fun `copy creates independent state`() {
        val original = CheatGiftState()
        val modified = original.copy(clickCount = 3)
        assertEquals(0, original.clickCount)
        assertEquals(3, modified.clickCount)
    }

    @Test
    fun `equals compares all fields`() {
        val a = CheatGiftState(clickCount = 2, hasResponded = true)
        val b = CheatGiftState(clickCount = 2, hasResponded = true)
        val c = CheatGiftState(clickCount = 3, hasResponded = true)
        assertEquals(a, b)
        assertEquals(false, a == c)
    }
}
