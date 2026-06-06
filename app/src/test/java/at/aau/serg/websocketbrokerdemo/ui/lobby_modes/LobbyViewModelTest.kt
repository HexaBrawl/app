package at.aau.serg.websocketbrokerdemo.ui.lobby_modes

import at.aau.serg.websocketbrokerdemo.data.serverside.GameMode
import at.aau.serg.websocketbrokerdemo.data.serverside.RoomDTO
import at.aau.serg.websocketbrokerdemo.network.RoomApiClient
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests fuer LobbyViewModel.
 *
 * Verwendet MockK fuer den RoomApiClient und den UnconfinedTestDispatcher
 * fuer Coroutines, damit Effekte sofort im State sichtbar werden.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LobbyViewModelTest {

    private lateinit var apiClient: RoomApiClient
    private lateinit var vm: LobbyViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        apiClient = mockk()
        vm = LobbyViewModel(apiClient)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ---- Initialer State -----------------------------------------------

    @Test
    fun `initial state has dialog closed and empty code`() {
        val state = vm.state.value
        assertFalse(state.showJoinDialog)
        assertEquals("", state.code)
        assertNull(state.error)
        assertFalse(state.isLoading)
    }

    // ---- Dialog-Lifecycle ----------------------------------------------

    @Test
    fun `openJoinDialog sets showJoinDialog to true`() {
        vm.openJoinDialog()
        assertTrue(vm.state.value.showJoinDialog)
    }

    @Test
    fun `openJoinDialog resets the code field and error`() {
        vm.onCodeChange("ABCD")
        vm.closeJoinDialog()
        vm.openJoinDialog()
        assertEquals("", vm.state.value.code)
        assertNull(vm.state.value.error)
    }

    // ---- Beitritts-Aktionen --------------------------------------------

    @Test
    fun `createPrivateGame calls API and sets state`() {
        val room = RoomDTO(joinCode = "ROOM1", mode = GameMode.DUAL_VALLEY, players = emptyList())
        coEvery { apiClient.createRoom(any()) } returns room

        vm.createPrivateGame(at.aau.serg.websocketbrokerdemo.ui.mainmenu.GameMode.DUAL_VALLEY)

        assertFalse(vm.state.value.isLoading)
        assertNull(vm.state.value.error)
    }

    @Test
    fun `createPrivateGame sets error when API fails`() {
        coEvery { apiClient.createRoom(any()) } returns null

        vm.createPrivateGame(at.aau.serg.websocketbrokerdemo.ui.mainmenu.GameMode.DUAL_VALLEY)

        assertFalse(vm.state.value.isLoading)
        assertEquals("Raum konnte nicht erstellt werden", vm.state.value.error)
    }

    @Test
    fun `tryJoinByCode calls API and closes dialog on success`() {
        vm.openJoinDialog()
        vm.onCodeChange("CODE1")
        val room = RoomDTO(joinCode = "CODE1", mode = GameMode.DUAL_VALLEY, players = emptyList())
        coEvery { apiClient.findByCode("CODE1") } returns room

        vm.tryJoinByCode()

        assertFalse(vm.state.value.showJoinDialog)
        assertNull(vm.state.value.error)
    }

    @Test
    fun `tryJoinByCode sets error on failure`() {
        vm.openJoinDialog()
        vm.onCodeChange("CODE1")
        coEvery { apiClient.findByCode("CODE1") } returns null

        vm.tryJoinByCode()

        assertTrue(vm.state.value.showJoinDialog)
        assertTrue(vm.state.value.error!!.contains("CODE1"))
    }
}
