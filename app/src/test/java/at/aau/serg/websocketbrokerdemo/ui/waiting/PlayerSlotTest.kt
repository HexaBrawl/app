package at.aau.serg.websocketbrokerdemo.ui.waiting.model

import androidx.compose.ui.graphics.Color
import at.aau.serg.websocketbrokerdemo.data.serverside.PlayerColor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Tests fuer PlayerSlot und PlayerColor (zentrale Variante).
 *
 * Die frueheren Wartelobby-spezifischen Tests fuer PlayerColor sind
 * obsolet -- der Enum lebt jetzt in `data.serverside`. Sanity-Checks
 * bleiben hier dass das vereinte Enum die fuer die Lobby noetigen
 * Properties (main, dark) trotzdem hat.
 */
class PlayerSlotTest {

    @Test
    fun `default slot is empty and not local`() {
        val slot = PlayerSlot(id = 1)
        assertEquals(SlotStatus.Empty, slot.status)
        assertEquals("", slot.name)
        assertFalse(slot.isLocal)
        assertFalse(slot.ready)
        assertEquals(PlayerColor.RED, slot.color)
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

    // ---- PlayerColor (zentral) ----------------------------------------

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
        assertEquals(PlayerColor.RED, PlayerColor.valueOf("RED"))
        assertEquals(PlayerColor.BLUE, PlayerColor.valueOf("BLUE"))
        assertEquals(PlayerColor.GREEN, PlayerColor.valueOf("GREEN"))
        assertEquals(PlayerColor.YELLOW, PlayerColor.valueOf("YELLOW"))
    }

    // ---- SlotStatus ---------------------------------------------------

    @Test
    fun `SlotStatus has exactly two entries`() {
        assertEquals(2, SlotStatus.entries.size)
    }

    @Test
    fun `SlotStatus contains Empty and Player`() {
        val names = SlotStatus.entries.map { it.name }.toSet()
        assertTrue("Empty" in names)
        assertTrue("Player" in names)
    }
}
