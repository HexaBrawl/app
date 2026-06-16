package at.aau.serg.websocketbrokerdemo.ui.lobby_modes

import androidx.compose.runtime.mutableStateOf
import at.aau.serg.websocketbrokerdemo.data.serverside.GameMode
import at.aau.serg.websocketbrokerdemo.data.serverside.RoomDTO
import at.aau.serg.websocketbrokerdemo.ui.mainmenu.GameMode as UiGameMode
import at.aau.serg.websocketbrokerdemo.network.GameSession
import at.aau.serg.websocketbrokerdemo.network.RoomApiClient
import io.mockk.coEvery
import io.mockk.every
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
    private lateinit var session: GameSession
    private lateinit var vm: LobbyViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        apiClient = mockk()
        session = mockk(relaxed = true)
        // Mocking the activeRoomId + activeJoinCode states
        val roomIdState = mutableStateOf("")
        val joinCodeState = mutableStateOf("")
        every { session.activeRoomId } returns roomIdState
        every { session.activeJoinCode } returns joinCodeState

        vm = LobbyViewModel(apiClient, session)
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

        assertFalse(vm.isLoading.value)
        assertNull(vm.lastError.value)
    }

    // ---- Dialog-Lifecycle ----------------------------------------------

    @Test
    fun `openJoinDialog sets showJoinDialog to true`() {
        vm.openJoinDialog()
        assertTrue(vm.state.value.showJoinDialog)
    }

    @Test
    fun `openJoinDialog resets the code field and error`() {
        vm.onCodeChange("ABCDEF")
        vm.closeJoinDialog()
        vm.openJoinDialog()
        assertEquals("", vm.state.value.code)
        assertNull(vm.lastError.value)
    }

    // ---- Beitritts-Aktionen --------------------------------------------

    @Test
    fun `createRoom calls API, sets session and triggers success`() {
        val room = RoomDTO(
            roomId = "uuid-GUID1",
            joinCode = "CODE01",
            mode = GameMode.DUAL_VALLEY
        )
        coEvery { apiClient.createRoom(any()) } returns room
        var successCalled = false

        vm.createRoom(at.aau.serg.websocketbrokerdemo.ui.mainmenu.GameMode.DUAL_VALLEY) {
            successCalled = true
        }

        assertFalse(vm.isLoading.value)
        assertNull(vm.lastError.value)
        assertEquals("uuid-GUID1", session.activeRoomId.value)
        assertEquals("CODE01", session.activeJoinCode.value)
        assertTrue(successCalled)
    }

    @Test
    fun `createRoom sets error when API fails`() {
        coEvery { apiClient.createRoom(any()) } returns null

        vm.createRoom(at.aau.serg.websocketbrokerdemo.ui.mainmenu.GameMode.DUAL_VALLEY) {}

        assertFalse(vm.isLoading.value)
        assertEquals("Raum konnte nicht erstellt werden", vm.lastError.value)
    }

    @Test
    fun `createRoom handles exception`() {
        coEvery { apiClient.createRoom(any()) } throws Exception("Network down")

        vm.createRoom(at.aau.serg.websocketbrokerdemo.ui.mainmenu.GameMode.DUAL_VALLEY) {}

        assertFalse(vm.isLoading.value)
        assertTrue(vm.lastError.value!!.contains("Network down"))
    }

    @Test
    fun `tryJoinByCodeAsync calls API, sets session and closes dialog on success`() {
        vm.openJoinDialog()
        vm.onCodeChange("CODE12") // 6 chars
        val room = RoomDTO(
            roomId = "uuid-GUID2",
            joinCode = "CODE12",
            mode = GameMode.DUAL_VALLEY
        )
        coEvery { apiClient.findByCode("CODE12") } returns room
        var successCalled = false

        vm.tryJoinByCodeAsync(UiGameMode.DUAL_VALLEY) {
            successCalled = true
        }

        assertFalse(vm.state.value.showJoinDialog)
        assertNull(vm.lastError.value)
        assertEquals("uuid-GUID2", session.activeRoomId.value)
        assertEquals("CODE12", session.activeJoinCode.value)
        assertTrue(successCalled)
    }

    @Test
    fun `tryJoinByCodeAsync sets error on failure`() {
        vm.openJoinDialog()
        vm.onCodeChange("CODE12")
        coEvery { apiClient.findByCode("CODE12") } returns null

        vm.tryJoinByCodeAsync(UiGameMode.DUAL_VALLEY) {}

        assertTrue(vm.state.value.showJoinDialog)
        assertTrue(vm.lastError.value!!.contains("CODE12"))
    }
}
