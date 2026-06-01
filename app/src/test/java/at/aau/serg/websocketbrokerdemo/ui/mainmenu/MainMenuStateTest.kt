package at.aau.serg.websocketbrokerdemo.ui.mainmenu

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Tests für die UI-Datenklasse MainMenuState.
 *
 * Minimal-Tests die sicherstellen dass Defaults stimmen und copy() macht
 * was es soll. Fängt versehentliche Default-Änderungen, die sonst zu
 * komischen UI-States führen würden ("warum ist beim Start immer der
 * Info-Dialog offen?").
 */
class MainMenuStateTest {

    @Test
    fun `default state shows no dialog and no pending mode`() {
        val state = MainMenuState()
        assertNull(state.pendingMode)
        assertFalse(state.showInfo)
    }

    @Test
    fun `copy preserves untouched fields`() {
        val original = MainMenuState(pendingMode = GameMode.DUAL_VALLEY, showInfo = false)
        val updated = original.copy(showInfo = true)

        assertEquals(GameMode.DUAL_VALLEY, updated.pendingMode)
        assertTrue(updated.showInfo)
    }

    @Test
    fun `equals returns true for identical states`() {
        val a = MainMenuState(pendingMode = GameMode.TRIAD_OUTPOST, showInfo = true)
        val b = MainMenuState(pendingMode = GameMode.TRIAD_OUTPOST, showInfo = true)
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }
}
