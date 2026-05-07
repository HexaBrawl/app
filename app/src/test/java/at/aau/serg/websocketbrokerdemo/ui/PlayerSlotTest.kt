package at.aau.serg.websocketbrokerdemo.ui.waiting

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PlayerSlotTest {

    @Test
    fun `default slot is empty and not local`() {
        val slot = PlayerSlot(id = 0)
        assertEquals(SlotStatus.Empty, slot.status)
        assertEquals("", slot.name)
        assertFalse(slot.ready)
        assertFalse(slot.isLocal)
    }

    @Test
    fun `local player slot can be created with name and color`() {
        val slot = PlayerSlot(
            id = 0,
            status = SlotStatus.Player,
            name = "General",
            color = PlayerColor.Blue,
            isLocal = true
        )
        assertEquals("General", slot.name)
        assertEquals(PlayerColor.Blue, slot.color)
        assertTrue(slot.isLocal)
        assertFalse(slot.ready)
    }

    @Test
    fun `copy preserves untouched fields`() {
        val original = PlayerSlot(id = 1, status = SlotStatus.Player, name = "Alice", color = PlayerColor.Green)
        val updated = original.copy(ready = true)

        assertEquals("Alice", updated.name)
        assertEquals(PlayerColor.Green, updated.color)
        assertEquals(SlotStatus.Player, updated.status)
        assertTrue(updated.ready)
    }

    @Test
    fun `slots with different ids are not equal`() {
        val a = PlayerSlot(id = 0)
        val b = PlayerSlot(id = 1)
        assertNotEquals(a, b)
    }

    @Test
    fun `equal slots have same hashCode`() {
        val a = PlayerSlot(id = 2, name = "Bob", color = PlayerColor.Yellow, status = SlotStatus.Bot)
        val b = PlayerSlot(id = 2, name = "Bob", color = PlayerColor.Yellow, status = SlotStatus.Bot)
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }
}

class PlayerColorTest {

    @Test
    fun `four standard colors exist`() {
        assertEquals(4, PlayerColor.entries.size)
    }

    @Test
    fun `each color has main and dark distinct`() {
        PlayerColor.entries.forEach { c ->
            assertNotEquals(c.main, c.dark, "main and dark should differ for $c")
        }
    }

    @Test
    fun `all main colors are unique`() {
        val mains = PlayerColor.entries.map { it.main }.toSet()
        assertEquals(PlayerColor.entries.size, mains.size)
    }

    @Test
    fun `valueOf works for all named colors`() {
        assertNotNull(PlayerColor.valueOf("Red"))
        assertNotNull(PlayerColor.valueOf("Blue"))
        assertNotNull(PlayerColor.valueOf("Green"))
        assertNotNull(PlayerColor.valueOf("Yellow"))
    }
}

class SlotStatusTest {

    @Test
    fun `three statuses exist`() {
        assertEquals(3, SlotStatus.entries.size)
    }

    @Test
    fun `statuses can be compared`() {
        assertEquals(SlotStatus.Empty, SlotStatus.Empty)
        assertNotEquals(SlotStatus.Empty, SlotStatus.Player)
        assertNotEquals(SlotStatus.Bot, SlotStatus.Player)
    }
}
