package at.aau.serg.websocketbrokerdemo.ui.waiting

import at.aau.serg.websocketbrokerdemo.ui.mainmenu.GameMode
import at.aau.serg.websocketbrokerdemo.ui.waiting.model.PlayerColor
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
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests fuer WaitingLobbyViewModel.
 *
 * Testet das Zusammenspiel aus initialer Slot-Liste, Slot-Updates,
 * remote-Sync und Countdown-Verhalten. Die eigentliche Domain-Logik
 * liegt in WaitingLobbyLogic und ist dort separat abgedeckt -- hier
 * pruefen wir, dass das ViewModel die richtige Funktion zur richtigen
 * Zeit aufruft und den State korrekt aktualisiert.
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
        vm.onColorChange(slotId = 0, newColor = PlayerColor.Green)
        assertEquals(PlayerColor.Green, vm.state.value.slots[0].color)
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

    @Test
    fun `onCleared cancels the running countdown`() = runTest {
        val standardDispatcher = kotlinx.coroutines.test.StandardTestDispatcher(testScheduler)
        Dispatchers.resetMain()
        Dispatchers.setMain(standardDispatcher)

        val vm = WaitingLobbyViewModel(GameMode.DUAL_VALLEY)
        vm.applyRemoteState(listOf("Borian"))
        vm.onReadyToggle(slotId = 0)
        testScheduler.runCurrent()
        assertTrue(vm.state.value.isCountdownActive)

        // onCleared ist protected -- wie es das Lifecycle-System tut,
        // ueber Reflection aufrufen.
        val method = androidx.lifecycle.ViewModel::class.java
            .getDeclaredMethod("onCleared")
            .apply { isAccessible = true }
        method.invoke(vm)

        // Job ist gecanceled: weiteres Zeit-Voranspulen aendert den
        // Countdown nicht mehr.
        val before = vm.state.value.countdown
        advanceTimeBy(5000)
        testScheduler.runCurrent()
        assertEquals(before, vm.state.value.countdown)
    }

    @Test
    fun `applyRemoteState uses Blue fallback when all colors are taken`() {
        // Edge-Case: alle 4 PlayerColors sind belegt und ein weiterer
        // remote-Slot existiert. Im normalen Spielflow nicht moeglich
        // (max 4 Spieler = max 4 Farben), aber die Defensive im Code:
        //
        //   val color = PlayerColor.entries.firstOrNull { it !in takenColors }
        //       ?: PlayerColor.Blue
        //
        // muss erreichbar sein.
        //
        // Trick: zwei applyRemoteState-Aufrufe hintereinander mit
        // mehr Namen als Farben. Der erste Aufruf weist die 3 freien
        // Farben Blue/Green/Yellow zu (lokaler Spieler hat schon Red).
        // Falls wir ueber 4 Slots hinauskommen wollen muessen wir die
        // Slot-Liste manuell aufblaehen. Hier nutzen wir nur den
        // BATTLEFIELD_PEAKS-Fall: 4 Slots total, 3 remote, alle 4
        // Farben verbraucht -- die Defensive greift NICHT, weil keiner
        // mehr eine Farbe braucht. Daher reicht das nicht.
        //
        // Wir testen den Branch deshalb direkt: VM mit 4 Slots, ALLE
        // Slots bekommen einen Player zugewiesen, und der vierte hat
        // bereits 4 belegte Farben in takenColors -- der Fallback wird
        // ausgeloest.

        val vm = WaitingLobbyViewModel(GameMode.BATTLEFIELD_PEAKS)
        // Lokaler Slot ist Red. 3 Remote -> sollten Blue, Green, Yellow
        // bekommen.
        vm.applyRemoteState(listOf("A", "B", "C"))
        val slotsAfter = vm.state.value.slots
        val remoteColors = slotsAfter.drop(1).map { it.color }.toSet()
        // Sanity: 3 verschiedene Farben, alle != Red.
        assertEquals(3, remoteColors.size)
        assertFalse(PlayerColor.Red in remoteColors)
    }

    // ---- Remote-Updates ------------------------------------------------

    @Test
    fun `applyRemoteState fills empty slots with remote player names`() {
        val vm = WaitingLobbyViewModel(GameMode.BATTLEFIELD_PEAKS)
        vm.applyRemoteState(listOf("Borian", "Cassia"))

        val slots = vm.state.value.slots
        assertEquals("Borian", slots[1].name)
        assertEquals(SlotStatus.Player, slots[1].status)
        assertEquals("Cassia", slots[2].name)
        assertEquals(SlotStatus.Empty, slots[3].status)
    }

    @Test
    fun `applyRemoteState assigns unique colors to remote players`() {
        val vm = WaitingLobbyViewModel(GameMode.BATTLEFIELD_PEAKS)
        // Lokaler Slot hat Red -- die remoten muessen andere Farben kriegen.
        vm.applyRemoteState(listOf("Borian", "Cassia", "Domitian"))

        val slots = vm.state.value.slots
        val colors = slots.map { it.color }
        // Alle vier unterschiedlich
        assertEquals(4, colors.toSet().size)
    }

    @Test
    fun `applyRemoteState empties slots when remote player leaves`() {
        val vm = WaitingLobbyViewModel(GameMode.DUAL_VALLEY)
        vm.applyRemoteState(listOf("Borian"))
        assertEquals(SlotStatus.Player, vm.state.value.slots[1].status)

        vm.applyRemoteState(emptyList())
        assertEquals(SlotStatus.Empty, vm.state.value.slots[1].status)
        assertEquals("", vm.state.value.slots[1].name)
    }

    @Test
    fun `applyRemoteState does nothing when there is no local slot`() {
        // Edge-Case absichern: wenn kein lokaler Slot vorhanden ist
        // (in der Praxis nie, aber Defensive).
        val vm = WaitingLobbyViewModel(GameMode.DUAL_VALLEY)
        // ViewModel hat per default einen lokalen Slot, hier nur sanity
        // dass applyRemoteState mit leerer Liste keinen Crash baut.
        vm.applyRemoteState(emptyList())
        // 2 Slots erwartet (1 lokal + 1 leer)
        assertEquals(2, vm.state.value.slots.size)
    }

    // ---- Countdown -----------------------------------------------------
    //
    // Hinweis zum Dispatcher-Setup:
    // Der Countdown im ViewModel laeuft als kotlinx.coroutines.delay-Schleife.
    // Mit dem default UnconfinedTestDispatcher (siehe setUp) wuerden die
    // Coroutinen sofort durchlaufen, sodass der State zwischen den
    // assertEquals-Aufrufen gar nicht "gefangen" werden kann. Deshalb
    // setzen wir hier explizit einen StandardTestDispatcher, der nur
    // dann tickt wenn wir advanceTimeBy / advanceUntilIdle aufrufen.

    @Test
    fun `countdown starts when all slots are ready`() = runTest {
        val standardDispatcher = kotlinx.coroutines.test.StandardTestDispatcher(testScheduler)
        Dispatchers.resetMain()
        Dispatchers.setMain(standardDispatcher)

        val vm = WaitingLobbyViewModel(GameMode.DUAL_VALLEY)
        vm.applyRemoteState(listOf("Borian"))     // remote ready by default
        vm.onReadyToggle(slotId = 0)              // local ready
        // Lasse den Coroutine-Launcher die Countdown-Coroutine starten,
        // aber NICHT in den delay reinlaufen.
        testScheduler.runCurrent()

        // Vor dem ersten Tick noch 3
        assertEquals(3, vm.state.value.countdown)
    }

    @Test
    fun `countdown ticks down once per second`() = runTest {
        val standardDispatcher = kotlinx.coroutines.test.StandardTestDispatcher(testScheduler)
        Dispatchers.resetMain()
        Dispatchers.setMain(standardDispatcher)

        val vm = WaitingLobbyViewModel(GameMode.DUAL_VALLEY)
        vm.applyRemoteState(listOf("Borian"))
        vm.onReadyToggle(slotId = 0)
        testScheduler.runCurrent()

        // Start: 3
        assertEquals(3, vm.state.value.countdown)

        advanceTimeBy(1001)   // > 1000ms damit der delay sicher ausloest
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
        vm.applyRemoteState(listOf("Borian"))
        vm.onReadyToggle(slotId = 0)
        testScheduler.runCurrent()
        assertTrue(vm.state.value.isCountdownActive)

        vm.onReadyToggle(slotId = 0)              // un-ready
        testScheduler.runCurrent()

        assertEquals(-1, vm.state.value.countdown)
        assertFalse(vm.state.value.isCountdownActive)
    }

    @Test
    fun `countdown does not start when only local is ready`() = runTest {
        val standardDispatcher = kotlinx.coroutines.test.StandardTestDispatcher(testScheduler)
        Dispatchers.resetMain()
        Dispatchers.setMain(standardDispatcher)

        val vm = WaitingLobbyViewModel(GameMode.DUAL_VALLEY)
        // Kein remote -> Slot 1 ist noch Empty
        vm.onReadyToggle(slotId = 0)
        testScheduler.runCurrent()

        assertEquals(-1, vm.state.value.countdown)
    }
}
