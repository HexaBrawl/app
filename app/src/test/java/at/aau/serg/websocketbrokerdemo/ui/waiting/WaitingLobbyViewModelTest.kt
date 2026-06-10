package at.aau.serg.websocketbrokerdemo.ui.waiting

import at.aau.serg.websocketbrokerdemo.data.serverside.Player
import at.aau.serg.websocketbrokerdemo.data.serverside.PlayerColor
import at.aau.serg.websocketbrokerdemo.ui.mainmenu.GameMode
import at.aau.serg.websocketbrokerdemo.ui.waiting.model.SlotStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests fuer WaitingLobbyViewModel.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class WaitingLobbyViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ---- Initialer State -----------------------------------------------

    @Test
    fun `initial state has correct slot count for DUAL_VALLEY`() {
        val vm = WaitingLobbyViewModel(GameMode.DUAL_VALLEY)
        assertEquals(2, vm.state.value.slots.size)
    }

    @Test
    fun `initial state has correct slot count for BATTLEFIELD_PEAKS`() {
        val vm = WaitingLobbyViewModel(GameMode.BATTLEFIELD_PEAKS)
        assertEquals(4, vm.state.value.slots.size)
    }

    @Test
    fun `initial state has countdown -1`() {
        val vm = WaitingLobbyViewModel(GameMode.DUAL_VALLEY)
        assertEquals(-1, vm.state.value.countdown)
        assertFalse(vm.state.value.isCountdownActive)
    }

    @Test
    fun `localName returns the local slot name`() {
        val vm = WaitingLobbyViewModel(GameMode.DUAL_VALLEY)
        assertTrue(vm.localName in WaitingLobbyLogic.GENERAL_NAMES)
    }

    @Test
    fun `localColor returns the local slot color`() {
        val vm = WaitingLobbyViewModel(GameMode.DUAL_VALLEY)
        // Initialer Slot 0 hat PlayerColor.RED.
        assertEquals(PlayerColor.RED, vm.localColor)
    }

    @Test
    fun `localColor reflects color changes`() {
        val vm = WaitingLobbyViewModel(GameMode.DUAL_VALLEY)
        vm.onColorChange(slotId = 0, newColor = PlayerColor.GREEN)
        assertEquals(PlayerColor.GREEN, vm.localColor)
    }

    @Test
    fun `localReady is false initially`() {
        val vm = WaitingLobbyViewModel(GameMode.DUAL_VALLEY)
        assertFalse(vm.localReady)
    }

    @Test
    fun `localReady becomes true after onReadyToggle`() {
        val vm = WaitingLobbyViewModel(GameMode.DUAL_VALLEY)
        vm.onReadyToggle(slotId = 0)
        assertTrue(vm.localReady)
    }

    @Test
    fun `clearLocalReady sets local slot ready to false`() {
        val vm = WaitingLobbyViewModel(GameMode.DUAL_VALLEY)
        vm.onReadyToggle(slotId = 0)
        assertTrue(vm.localReady)

        vm.clearLocalReady()
        assertFalse(vm.localReady)
    }

    @Test
    fun `clearLocalReady cancels a running countdown`() = runTest {
        val standardDispatcher = kotlinx.coroutines.test.StandardTestDispatcher(testScheduler)
        Dispatchers.resetMain()
        Dispatchers.setMain(standardDispatcher)

        val vm = WaitingLobbyViewModel(GameMode.DUAL_VALLEY)
        vm.applyRemoteState(listOf(Player(name = "Borian", color = PlayerColor.BLUE)))
        vm.onReadyToggle(slotId = 0)
        testScheduler.runCurrent()
        assertTrue(vm.state.value.isCountdownActive)

        vm.clearLocalReady()
        testScheduler.runCurrent()
        assertFalse(vm.state.value.isCountdownActive)
    }

    // ---- Fehler-Handling -----------------------------------------------

    @Test
    fun `showError sets the errorMessage`() {
        val vm = WaitingLobbyViewModel(GameMode.DUAL_VALLEY)
        vm.showError("Test-Fehler")
        assertEquals("Test-Fehler", vm.state.value.errorMessage)
    }

    @Test
    fun `clearError removes the errorMessage`() {
        val vm = WaitingLobbyViewModel(GameMode.DUAL_VALLEY)
        vm.showError("Test-Fehler")
        vm.clearError()
        assertNull(vm.state.value.errorMessage)
    }

    // ---- User-Aktionen -------------------------------------------------

    @Test
    fun `onNameChange updates the local slot name`() {
        val vm = WaitingLobbyViewModel(GameMode.DUAL_VALLEY)
        vm.onNameChange(slotId = 0, newName = "Neu")
        assertEquals("Neu", vm.state.value.slots[0].name)
    }

    @Test
    fun `onColorChange updates the local slot color`() {
        val vm = WaitingLobbyViewModel(GameMode.DUAL_VALLEY)
        vm.onColorChange(slotId = 0, newColor = PlayerColor.GREEN)
        assertEquals(PlayerColor.GREEN, vm.state.value.slots[0].color)
    }

    @Test
    fun `onReadyToggle flips ready when name is present`() {
        val vm = WaitingLobbyViewModel(GameMode.DUAL_VALLEY)
        vm.onReadyToggle(slotId = 0)
        assertTrue(vm.state.value.slots[0].ready)
    }

    @Test
    fun `onReadyToggle does nothing when name is blank`() {
        val vm = WaitingLobbyViewModel(GameMode.DUAL_VALLEY)
        vm.onNameChange(slotId = 0, newName = "")
        vm.onReadyToggle(slotId = 0)
        assertFalse(vm.state.value.slots[0].ready)
    }

    // ---- Remote-Updates ------------------------------------------------

    @Test
    fun `applyRemoteState fills empty slots with remote player names`() {
        val vm = WaitingLobbyViewModel(GameMode.BATTLEFIELD_PEAKS)
        vm.applyRemoteState(
            listOf(
                Player(name = "Borian", color = PlayerColor.BLUE),
                Player(name = "Cassia", color = PlayerColor.GREEN)
            )
        )

        val slots = vm.state.value.slots
        assertEquals("Borian", slots[1].name)
        assertEquals(SlotStatus.Player, slots[1].status)
        assertEquals("Cassia", slots[2].name)
        assertEquals(SlotStatus.Empty, slots[3].status)
    }

    @Test
    fun `applyRemoteState uses the colors from the server`() {
        val vm = WaitingLobbyViewModel(GameMode.BATTLEFIELD_PEAKS)
        vm.applyRemoteState(
            listOf(
                Player(name = "Borian", color = PlayerColor.BLUE),
                Player(name = "Cassia", color = PlayerColor.GREEN),
                Player(name = "Domitian", color = PlayerColor.YELLOW)
            )
        )

        val slots = vm.state.value.slots
        // Slots 1..3 spiegeln die Server-Farben wider, nicht eine
        // client-seitige Vergabe.
        assertEquals(PlayerColor.BLUE, slots[1].color)
        assertEquals(PlayerColor.GREEN, slots[2].color)
        assertEquals(PlayerColor.YELLOW, slots[3].color)
    }

    @Test
    fun `applyRemoteState reassigns local color if it collides with remote`() {
        val vm = WaitingLobbyViewModel(GameMode.DUAL_VALLEY)
        // Lokaler Slot 0 startet mit RED (default).
        // Remote Player hat auch RED -> Auto-Reassign muss greifen.
        vm.applyRemoteState(listOf(Player(name = "Borian", color = PlayerColor.RED)))
        assertNotEquals(PlayerColor.RED, vm.localColor)
    }

    @Test
    fun `applyRemoteState does not reassign local color when user is ready`() {
        val vm = WaitingLobbyViewModel(GameMode.DUAL_VALLEY)
        vm.onReadyToggle(slotId = 0)   // user wird ready mit default RED
        vm.applyRemoteState(listOf(Player(name = "Borian", color = PlayerColor.RED)))
        // Soll NICHT geaendert werden -- der User hat bewusst gewaehlt.
        assertEquals(PlayerColor.RED, vm.localColor)
    }

    @Test
    fun `applyRemoteState empties slots when remote player leaves`() {
        val vm = WaitingLobbyViewModel(GameMode.DUAL_VALLEY)
        vm.applyRemoteState(listOf(Player(name = "Borian", color = PlayerColor.BLUE)))
        assertEquals(SlotStatus.Player, vm.state.value.slots[1].status)

        vm.applyRemoteState(emptyList())
        assertEquals(SlotStatus.Empty, vm.state.value.slots[1].status)
        assertEquals("", vm.state.value.slots[1].name)
    }

    @Test
    fun `applyRemoteState does nothing when there is no local slot`() {
        val vm = WaitingLobbyViewModel(GameMode.DUAL_VALLEY)
        vm.applyRemoteState(emptyList())
        assertEquals(2, vm.state.value.slots.size)
    }

    // ---- Countdown -----------------------------------------------------

    @Test
    fun `countdown starts when all slots are ready`() = runTest {
        val standardDispatcher = kotlinx.coroutines.test.StandardTestDispatcher(testScheduler)
        Dispatchers.resetMain()
        Dispatchers.setMain(standardDispatcher)

        val vm = WaitingLobbyViewModel(GameMode.DUAL_VALLEY)
        vm.applyRemoteState(listOf(Player(name = "Borian", color = PlayerColor.BLUE)))
        vm.onReadyToggle(slotId = 0)
        testScheduler.runCurrent()

        assertEquals(3, vm.state.value.countdown)
    }

    @Test
    fun `countdown ticks down once per second`() = runTest {
        val standardDispatcher = kotlinx.coroutines.test.StandardTestDispatcher(testScheduler)
        Dispatchers.resetMain()
        Dispatchers.setMain(standardDispatcher)

        val vm = WaitingLobbyViewModel(GameMode.DUAL_VALLEY)
        vm.applyRemoteState(listOf(Player(name = "Borian", color = PlayerColor.BLUE)))
        vm.onReadyToggle(slotId = 0)
        testScheduler.runCurrent()

        assertEquals(3, vm.state.value.countdown)

        advanceTimeBy(1001)
        testScheduler.runCurrent()
        assertEquals(2, vm.state.value.countdown)

        advanceTimeBy(1001)
        testScheduler.runCurrent()
        assertEquals(1, vm.state.value.countdown)

        advanceTimeBy(1001)
        testScheduler.runCurrent()
        assertEquals(0, vm.state.value.countdown)
    }

    @Test
    fun `countdown cancels when a player goes back to not-ready`() = runTest {
        val standardDispatcher = kotlinx.coroutines.test.StandardTestDispatcher(testScheduler)
        Dispatchers.resetMain()
        Dispatchers.setMain(standardDispatcher)

        val vm = WaitingLobbyViewModel(GameMode.DUAL_VALLEY)
        vm.applyRemoteState(listOf(Player(name = "Borian", color = PlayerColor.BLUE)))
        vm.onReadyToggle(slotId = 0)
        testScheduler.runCurrent()
        assertTrue(vm.state.value.isCountdownActive)

        vm.onReadyToggle(slotId = 0)
        testScheduler.runCurrent()

        assertEquals(-1, vm.state.value.countdown)
        assertFalse(vm.state.value.isCountdownActive)
    }

    @Test
    fun `countdownComplete becomes true after countdown finishes`() = runTest {
        val standardDispatcher = kotlinx.coroutines.test.StandardTestDispatcher(testScheduler)
        Dispatchers.resetMain()
        Dispatchers.setMain(standardDispatcher)

        val vm = WaitingLobbyViewModel(GameMode.DUAL_VALLEY)
        vm.applyRemoteState(listOf(Player(name = "Borian", color = PlayerColor.BLUE)))
        vm.onReadyToggle(slotId = 0)
        testScheduler.runCurrent()
        assertFalse(vm.state.value.countdownComplete)

        advanceTimeBy(3500)
        testScheduler.runCurrent()
        assertTrue(vm.state.value.countdownComplete)
    }

    @Test
    fun `countdownComplete resets to false when countdown cancels`() = runTest {
        val standardDispatcher = kotlinx.coroutines.test.StandardTestDispatcher(testScheduler)
        Dispatchers.resetMain()
        Dispatchers.setMain(standardDispatcher)

        val vm = WaitingLobbyViewModel(GameMode.DUAL_VALLEY)
        vm.applyRemoteState(listOf(Player(name = "Borian", color = PlayerColor.BLUE)))
        vm.onReadyToggle(slotId = 0)
        testScheduler.runCurrent()

        // User klickt Ready wieder weg -> Countdown wird gecancelt
        vm.onReadyToggle(slotId = 0)
        testScheduler.runCurrent()

        assertFalse(vm.state.value.countdownComplete)
    }

    @Test
    fun `countdown does not start when only local is ready`() = runTest {
        val standardDispatcher = kotlinx.coroutines.test.StandardTestDispatcher(testScheduler)
        Dispatchers.resetMain()
        Dispatchers.setMain(standardDispatcher)

        val vm = WaitingLobbyViewModel(GameMode.DUAL_VALLEY)
        vm.onReadyToggle(slotId = 0)
        testScheduler.runCurrent()

        assertEquals(-1, vm.state.value.countdown)
    }

    @Test
    fun `onCleared cancels the running countdown`() = runTest {
        val standardDispatcher = kotlinx.coroutines.test.StandardTestDispatcher(testScheduler)
        Dispatchers.resetMain()
        Dispatchers.setMain(standardDispatcher)

        val vm = WaitingLobbyViewModel(GameMode.DUAL_VALLEY)
        vm.applyRemoteState(listOf(Player(name = "Borian", color = PlayerColor.BLUE)))
        vm.onReadyToggle(slotId = 0)
        testScheduler.runCurrent()
        assertTrue(vm.state.value.isCountdownActive)

        val method = androidx.lifecycle.ViewModel::class.java
            .getDeclaredMethod("onCleared")
            .apply { isAccessible = true }
        method.invoke(vm)

        val before = vm.state.value.countdown
        advanceTimeBy(5000)
        testScheduler.runCurrent()
        assertEquals(before, vm.state.value.countdown)
    }
}
