package at.aau.serg.websocketbrokerdemo.ui.mainmenu

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests für MainMenuViewModel.
 *
 * Kein Coroutine-Setup nötig: das ViewModel hat keine Repository-/IO-
 * Abhängigkeiten, nur MutableStateFlow-Updates. Wir prüfen einfach
 * dass jeder onXxx-Aufruf den State korrekt weiterschiebt.
 */
class MainMenuViewModelTest {

    private lateinit var vm: MainMenuViewModel

    @BeforeEach
    fun setUp() {
        vm = MainMenuViewModel()
    }

    @Test
    fun `initial state has no dialogs visible`() {
        // Beim App-Start oder nach Rotation sollte das Hauptmenü ohne
        // offene Popups erscheinen.
        val state = vm.state.value
        assertNull(state.pendingMode)
        assertFalse(state.showInfo)
    }

    // ---- pendingMode ----------------------------------------------------

    @Test
    fun `onHotspotTapped sets pendingMode`() {
        vm.onHotspotTapped(GameMode.DUAL_VALLEY)
        assertEquals(GameMode.DUAL_VALLEY, vm.state.value.pendingMode)
    }

    @Test
    fun `onHotspotTapped can overwrite previous pendingMode`() {
        // Der User darf von einem Hotspot zum anderen wechseln, bevor er
        // bestätigt -- der State soll dem letzten Tap folgen.
        vm.onHotspotTapped(GameMode.DUAL_VALLEY)
        vm.onHotspotTapped(GameMode.TRIAD_OUTPOST)
        assertEquals(GameMode.TRIAD_OUTPOST, vm.state.value.pendingMode)
    }

    @Test
    fun `onDismissPendingMode clears pendingMode`() {
        vm.onHotspotTapped(GameMode.DUAL_VALLEY)
        vm.onDismissPendingMode()
        assertNull(vm.state.value.pendingMode)
    }

    @Test
    fun `onConfirmPendingMode clears pendingMode`() {
        // Beim Bestätigen wird das Popup geschlossen -- die eigentliche
        // Navigation passiert im Composable über MainMenuLogic.
        vm.onHotspotTapped(GameMode.BATTLEFIELD_PEAKS)
        vm.onConfirmPendingMode()
        assertNull(vm.state.value.pendingMode)
    }

    // ---- showInfo -------------------------------------------------------

    @Test
    fun `onInfoClicked sets showInfo to true`() {
        vm.onInfoClicked()
        assertTrue(vm.state.value.showInfo)
    }

    @Test
    fun `onDismissInfo sets showInfo to false`() {
        vm.onInfoClicked()
        vm.onDismissInfo()
        assertFalse(vm.state.value.showInfo)
    }

    @Test
    fun `pendingMode and showInfo are independent`() {
        // Beide Flags können theoretisch gleichzeitig wahr sein -- der UI-
        // Code entscheidet welcher Dialog Priorität hat. Hier prüfen wir,
        // dass die State-Updates sich nicht gegenseitig löschen.
        vm.onInfoClicked()
        vm.onHotspotTapped(GameMode.DUAL_VALLEY)

        assertTrue(vm.state.value.showInfo)
        assertEquals(GameMode.DUAL_VALLEY, vm.state.value.pendingMode)
    }
}
