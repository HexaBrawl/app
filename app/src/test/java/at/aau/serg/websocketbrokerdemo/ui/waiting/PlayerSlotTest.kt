package at.aau.serg.websocketbrokerdemo.ui.waiting.model

import androidx.compose.ui.graphics.Color
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Tests fuer PlayerSlot, PlayerColor und SlotStatus.
 *
 * Reine Daten-Tests: Defaults, copy(), Enum-Sanity.
 */
class PlayerSlotTest {

    // ---- PlayerSlot defaults ------------------------------------------

    @Test
    fun `default slot is empty and not local`() {
        val slot = PlayerSlot(id = 1)
        assertEquals(SlotStatus.Empty, slot.status)
        assertEquals("", slot.name)
        assertFalse(slot.isLocal)
        assertFalse(slot.ready)
        assertEquals(PlayerColor.Red, slot.color)
    }

    @Test
    fun `copy creates independent slot`() {
        val original = PlayerSlot(id = 1, name = "A")
        val modified = original.copy(name = "B")
        assertEquals("A", original.name)
        assertEquals("B", modified.name)
        assertEquals(original.id, modified.id)
    }

    @Test
    fun `equals compares all fields`() {
        val a = PlayerSlot(id = 1, name = "X")
        val b = PlayerSlot(id = 1, name = "X")
        val c = PlayerSlot(id = 1, name = "Y")
        assertEquals(a, b)
        assertNotEquals(a, c)
    }

    // ---- PlayerColor --------------------------------------------------

    @Test
    fun `there are exactly four player colors`() {
        assertEquals(4, PlayerColor.entries.size)
    }

    @Test
    fun `each player color has distinct main and dark`() {
        PlayerColor.entries.forEach { color ->
            assertNotEquals(
                color.main, color.dark,
                "${color.name}: main und dark muessen sich unterscheiden"
            )
        }
    }

    @Test
    fun `all player main colors are unique`() {
        val mains = PlayerColor.entries.map { it.main }
        assertEquals(mains.size, mains.toSet().size)
    }

    @Test
    fun `no player color is transparent`() {
        PlayerColor.entries.forEach { color ->
            assertTrue(color.main != Color.Transparent)
            assertTrue(color.dark != Color.Transparent)
        }
    }

    @Test
    fun `PlayerColor valueOf works for all entries`() {
        assertEquals(PlayerColor.Red, PlayerColor.valueOf("Red"))
        assertEquals(PlayerColor.Blue, PlayerColor.valueOf("Blue"))
        assertEquals(PlayerColor.Green, PlayerColor.valueOf("Green"))
        assertEquals(PlayerColor.Yellow, PlayerColor.valueOf("Yellow"))
    }

    // ---- SlotStatus ---------------------------------------------------

    @Test
    fun `SlotStatus has exactly two entries`() {
        // Empty + Player; Bot wurde im Refactoring entfernt.
        assertEquals(2, SlotStatus.entries.size)
    }

    @Test
    fun `SlotStatus contains Empty and Player`() {
        val names = SlotStatus.entries.map { it.name }.toSet()
        assertTrue("Empty" in names)
        assertTrue("Player" in names)
    }
}
