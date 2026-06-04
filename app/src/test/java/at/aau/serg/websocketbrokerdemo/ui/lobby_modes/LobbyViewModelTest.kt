package at.aau.serg.websocketbrokerdemo.ui.lobby_modes

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests fuer LobbyViewModel.
 *
 * Reine State-Maschine ohne Coroutines, daher kein Coroutine-Setup
 * noetig. Geprueft werden Dialog-Lifecycle, Code-Eingabe und der
 * Join-Versuch mit gueltigem/ungueltigem Code.
 */
class LobbyViewModelTest {

    private lateinit var vm: LobbyViewModel

    @BeforeEach
    fun setUp() {
        vm = LobbyViewModel()
    }

    // ---- Initialer State -----------------------------------------------

    @Test
    fun `initial state has dialog closed and empty code`() {
        val state = vm.state.value
        assertFalse(state.showJoinDialog)
        assertEquals("", state.code)
        assertFalse(state.canJoin)
    }

    // ---- Dialog-Lifecycle ----------------------------------------------

    @Test
    fun `openJoinDialog sets showJoinDialog to true`() {
        vm.openJoinDialog()
        assertTrue(vm.state.value.showJoinDialog)
    }

    @Test
    fun `openJoinDialog resets the code field`() {
        // Wenn der User schon mal getippt hat und den Dialog wieder
        // oeffnet, darf der alte Inhalt nicht stehenbleiben.
        vm.onCodeChange("ABCD")
        vm.closeJoinDialog()
        vm.openJoinDialog()
        assertEquals("", vm.state.value.code)
    }

    @Test
    fun `closeJoinDialog sets showJoinDialog to false`() {
        vm.openJoinDialog()
        vm.closeJoinDialog()
        assertFalse(vm.state.value.showJoinDialog)
    }

    // ---- Code-Eingabe --------------------------------------------------

    @Test
    fun `onCodeChange normalizes the input`() {
        vm.onCodeChange("ab cd 12!")
        assertEquals("ABCD12", vm.state.value.code)
    }

    @Test
    fun `onCodeChange caps at 8 characters`() {
        vm.onCodeChange("ABCDEFGHIJKL")
        assertEquals(8, vm.state.value.code.length)
        assertEquals("ABCDEFGH", vm.state.value.code)
    }

    @Test
    fun `onCodeChange to empty string clears the code`() {
        vm.onCodeChange("ABCD")
        vm.onCodeChange("")
        assertEquals("", vm.state.value.code)
    }

    // ---- tryJoinByCode -------------------------------------------------

    @Test
    fun `tryJoinByCode returns false for invalid code`() {
        vm.openJoinDialog()
        vm.onCodeChange("AB")    // zu kurz
        val result = vm.tryJoinByCode()
        assertFalse(result)
        // Dialog bleibt offen, der User soll seinen Code korrigieren koennen
        assertTrue(vm.state.value.showJoinDialog)
    }

    @Test
    fun `tryJoinByCode returns true for valid code and closes dialog`() {
        vm.openJoinDialog()
        vm.onCodeChange("ABCD")
        val result = vm.tryJoinByCode()
        assertTrue(result)
        assertFalse(vm.state.value.showJoinDialog)
    }

    @Test
    fun `tryJoinByCode returns false when code is empty`() {
        vm.openJoinDialog()
        val result = vm.tryJoinByCode()
        assertFalse(result)
    }

    @Test
    fun `tryJoinByCode succeeds at 8 character boundary`() {
        vm.openJoinDialog()
        vm.onCodeChange("ABCDEFGH")
        assertTrue(vm.tryJoinByCode())
    }
}
