package at.aau.serg.websocketbrokerdemo.ui.game.tophud

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests fuer TopHudViewModel. State-Maschine ohne Coroutines.
 */
class TopHudViewModelTest {

    private lateinit var vm: TopHudViewModel

    @BeforeEach
    fun setUp() {
        vm = TopHudViewModel()
    }

    @Test
    fun `initial popup is None`() {
        assertEquals(HudPopup.None, vm.state.value.popup)
    }

    @Test
    fun `openMenu sets popup to Menu`() {
        vm.openMenu()
        assertEquals(HudPopup.Menu, vm.state.value.popup)
    }

    @Test
    fun `showInfo sets popup to Info`() {
        vm.showInfo()
        assertEquals(HudPopup.Info, vm.state.value.popup)
    }

    @Test
    fun `closePopup resets popup to None`() {
        vm.openMenu()
        vm.closePopup()
        assertEquals(HudPopup.None, vm.state.value.popup)
    }

    @Test
    fun `showInfo from Menu replaces popup`() {
        vm.openMenu()
        vm.showInfo()
        assertEquals(HudPopup.Info, vm.state.value.popup)
    }

    @Test
    fun `showSettings sets popup to Settings`() {
        vm.showSettings()
        assertEquals(HudPopup.Settings, vm.state.value.popup)
    }
}
