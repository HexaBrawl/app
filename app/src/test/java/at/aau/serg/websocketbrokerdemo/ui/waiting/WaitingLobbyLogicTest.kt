package at.aau.serg.websocketbrokerdemo.ui.waiting

import at.aau.serg.websocketbrokerdemo.data.serverside.PlayerColor
import at.aau.serg.websocketbrokerdemo.ui.mainmenu.GameMode
import at.aau.serg.websocketbrokerdemo.ui.waiting.model.PlayerSlot
import at.aau.serg.websocketbrokerdemo.ui.waiting.model.SlotStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Tests fuer WaitingLobbyLogic.
 *
 * Reine Pure-Function-Tests. Nach der Enum-Vereinigung nutzen wir
 * RED/BLUE/GREEN/YELLOW statt der frueheren Red/Blue/Green/Yellow.
 */
class WaitingLobbyLogicTest {

    private val player0 = PlayerSlot(
        id = 0, status = SlotStatus.Player, name = "Aldric",
        color = PlayerColor.RED, ready = false, isLocal = true
    )
    private val player1 = PlayerSlot(
        id = 1, status = SlotStatus.Player, name = "Borian",
        color = PlayerColor.BLUE, ready = true, isLocal = false
    )
    private val empty2 = PlayerSlot(id = 2)

    // ---- createInitialSlots --------------------------------------------

    @Test
    fun `createInitialSlots returns playerCount slots for DUAL_VALLEY`() {
        val slots = WaitingLobbyLogic.createInitialSlots(GameMode.DUAL_VALLEY) { "Tester" }
        assertEquals(2, slots.size)
    }

    @Test
    fun `createInitialSlots returns playerCount slots for TRIAD_OUTPOST`() {
        val slots = WaitingLobbyLogic.createInitialSlots(GameMode.TRIAD_OUTPOST) { "Tester" }
        assertEquals(3, slots.size)
    }

    @Test
    fun `createInitialSlots returns playerCount slots for BATTLEFIELD_PEAKS`() {
        val slots = WaitingLobbyLogic.createInitialSlots(GameMode.BATTLEFIELD_PEAKS) { "Tester" }
        assertEquals(4, slots.size)
    }

    @Test
    fun `createInitialSlots puts local player in slot 0`() {
        val slots = WaitingLobbyLogic.createInitialSlots(GameMode.DUAL_VALLEY) { "Tester" }
        assertEquals(0, slots[0].id)
        assertTrue(slots[0].isLocal)
        assertEquals(SlotStatus.Player, slots[0].status)
        assertEquals("Tester", slots[0].name)
    }

    @Test
    fun `createInitialSlots fills other slots as empty`() {
        val slots = WaitingLobbyLogic.createInitialSlots(GameMode.BATTLEFIELD_PEAKS) { "Tester" }
        assertEquals(SlotStatus.Empty, slots[1].status)
        assertEquals(SlotStatus.Empty, slots[2].status)
        assertEquals(SlotStatus.Empty, slots[3].status)
    }

    @Test
    fun `createInitialSlots uses GENERAL_NAMES by default`() {
        val slots = WaitingLobbyLogic.createInitialSlots(GameMode.DUAL_VALLEY)
        assertTrue(slots[0].name in WaitingLobbyLogic.GENERAL_NAMES)
    }

    // ---- takenColorsExcept ---------------------------------------------

    @Test
    fun `takenColorsExcept returns colors of other non-empty slots`() {
        val slots = listOf(player0, player1, empty2)
        val taken = WaitingLobbyLogic.takenColorsExcept(slots, exceptId = 0)
        assertEquals(setOf(PlayerColor.BLUE), taken)
    }

    @Test
    fun `takenColorsExcept excludes the slot itself`() {
        val slots = listOf(player0, player1)
        val taken = WaitingLobbyLogic.takenColorsExcept(slots, exceptId = 1)
        assertEquals(setOf(PlayerColor.RED), taken)
    }

    @Test
    fun `takenColorsExcept ignores empty slots`() {
        val slots = listOf(player0, empty2)
        val taken = WaitingLobbyLogic.takenColorsExcept(slots, exceptId = 0)
        assertTrue(taken.isEmpty())
    }

    @Test
    fun `takenColorsExcept on empty list returns empty set`() {
        val taken = WaitingLobbyLogic.takenColorsExcept(emptyList(), exceptId = 0)
        assertTrue(taken.isEmpty())
    }

    // ---- allReady ------------------------------------------------------

    @Test
    fun `allReady is false when a slot is empty`() {
        val slots = listOf(player0.copy(ready = true), empty2)
        assertFalse(WaitingLobbyLogic.allReady(slots))
    }

    @Test
    fun `allReady is false when a player is not ready`() {
        val slots = listOf(player0, player1)
        assertFalse(WaitingLobbyLogic.allReady(slots))
    }

    @Test
    fun `allReady is true when all players are ready and no empty slots`() {
        val slots = listOf(player0.copy(ready = true), player1)
        assertTrue(WaitingLobbyLogic.allReady(slots))
    }

    @Test
    fun `allReady is false on empty list`() {
        assertFalse(WaitingLobbyLogic.allReady(emptyList()))
    }

    // ---- replaceSlot ---------------------------------------------------

    @Test
    fun `replaceSlot updates the matching slot`() {
        val slots = listOf(player0, player1)
        val updated = player0.copy(name = "Neu")
        val result = WaitingLobbyLogic.replaceSlot(slots, updated)
        assertEquals("Neu", result[0].name)
        assertEquals("Borian", result[1].name)
    }

    @Test
    fun `replaceSlot returns unchanged list when id not found`() {
        val slots = listOf(player0, player1)
        val ghost = PlayerSlot(id = 99, name = "Ghost")
        val result = WaitingLobbyLogic.replaceSlot(slots, ghost)
        assertEquals(slots, result)
    }

    // ---- applyNameChange -----------------------------------------------

    @Test
    fun `applyNameChange updates the name`() {
        val slots = listOf(player0)
        val result = WaitingLobbyLogic.applyNameChange(slots, slotId = 0, newName = "Neu")
        assertEquals("Neu", result[0].name)
    }

    @Test
    fun `applyNameChange does nothing when slot is ready`() {
        val slots = listOf(player0.copy(ready = true))
        val result = WaitingLobbyLogic.applyNameChange(slots, slotId = 0, newName = "Neu")
        assertEquals("Aldric", result[0].name)
    }

    @Test
    fun `applyNameChange does nothing for unknown slot id`() {
        val slots = listOf(player0)
        val result = WaitingLobbyLogic.applyNameChange(slots, slotId = 99, newName = "Ghost")
        assertEquals(slots, result)
    }

    // ---- applyColorChange ----------------------------------------------

    @Test
    fun `applyColorChange updates the color`() {
        val slots = listOf(player0)
        val result = WaitingLobbyLogic.applyColorChange(slots, slotId = 0, newColor = PlayerColor.GREEN)
        assertEquals(PlayerColor.GREEN, result[0].color)
    }

    @Test
    fun `applyColorChange does nothing when slot is ready`() {
        val slots = listOf(player0.copy(ready = true))
        val result = WaitingLobbyLogic.applyColorChange(slots, slotId = 0, newColor = PlayerColor.GREEN)
        assertEquals(PlayerColor.RED, result[0].color)
    }

    @Test
    fun `applyColorChange rejects color already taken by other slot`() {
        val slots = listOf(player0, player1)
        val result = WaitingLobbyLogic.applyColorChange(slots, slotId = 0, newColor = PlayerColor.BLUE)
        assertEquals(PlayerColor.RED, result[0].color)
    }

    @Test
    fun `applyColorChange does nothing for unknown slot id`() {
        val slots = listOf(player0)
        val result = WaitingLobbyLogic.applyColorChange(slots, slotId = 99, newColor = PlayerColor.GREEN)
        assertEquals(slots, result)
    }

    // ---- applyReadyToggle ----------------------------------------------

    @Test
    fun `applyReadyToggle flips ready from false to true`() {
        val slots = listOf(player0)
        val result = WaitingLobbyLogic.applyReadyToggle(slots, slotId = 0)
        assertTrue(result[0].ready)
    }

    @Test
    fun `applyReadyToggle flips ready from true to false`() {
        val slots = listOf(player0.copy(ready = true))
        val result = WaitingLobbyLogic.applyReadyToggle(slots, slotId = 0)
        assertFalse(result[0].ready)
    }

    @Test
    fun `applyReadyToggle does nothing on empty slot`() {
        val slots = listOf(empty2)
        val result = WaitingLobbyLogic.applyReadyToggle(slots, slotId = 2)
        assertFalse(result[0].ready)
    }

    @Test
    fun `applyReadyToggle does nothing when name is blank`() {
        val slots = listOf(player0.copy(name = "  "))
        val result = WaitingLobbyLogic.applyReadyToggle(slots, slotId = 0)
        assertFalse(result[0].ready)
    }

    @Test
    fun `applyReadyToggle does nothing for unknown slot id`() {
        val slots = listOf(player0)
        val result = WaitingLobbyLogic.applyReadyToggle(slots, slotId = 99)
        assertEquals(slots, result)
    }
}
