package at.aau.serg.websocketbrokerdemo.ui.game

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.IntSize
import at.aau.serg.websocketbrokerdemo.data.serverside.ErrorCode
import at.aau.serg.websocketbrokerdemo.data.serverside.ErrorMessage
import at.aau.serg.websocketbrokerdemo.data.serverside.GameUnit
import at.aau.serg.websocketbrokerdemo.data.serverside.Move
import at.aau.serg.websocketbrokerdemo.data.serverside.UnitType
import at.aau.serg.websocketbrokerdemo.network.GameSession
import at.aau.serg.websocketbrokerdemo.network.UnitMoveEndpoint
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests fuer GameViewModel.
 *
 * Das ViewModel haelt nur Auswahl-State und delegiert die Entscheidungs-
 * Logik an [GameScreenLogic]. Wir verifizieren hier:
 *  - Dass die richtigen Logic-Funktionen aufgerufen werden
 *  - Dass die GameSession beim Move-Senden korrekt angesprochen wird
 *  - Dass der State nach Aktionen passt
 */
class GameViewModelTest {

    private val alice = "Alice"
    private val bob = "Bob"

    private lateinit var endpoint: UnitMoveEndpoint
    private lateinit var session: GameSession
    private lateinit var vm: GameViewModel

    @BeforeEach
    fun setUp() {
        endpoint = mockk(relaxed = true)
        justRun { endpoint.sendMove(any(), any()) }
        justRun { endpoint.buyUnit(any(), any(), any(), any(), any()) }
        session = GameSession(
            endpoint = endpoint,
            activeRoomId = mutableStateOf("test-room"),
            gameState = mutableStateOf(null),
            lastError = mutableStateOf(null),
            localPlayerName = mutableStateOf(alice)
        )
        vm = GameViewModel(session)
    }

    private fun own(x: Int, y: Int) =
        GameUnit(player = alice, x = x, y = y, type = UnitType.INFANTRY)

    private fun enemy(x: Int, y: Int) =
        GameUnit(player = bob, x = x, y = y, type = UnitType.ARCHER)

    // ---- Initialer State -----------------------------------------------

    @Test
    fun `initial state has no selection and no placement`() {
        assertNull(vm.uiState.value.selected)
        assertNull(vm.uiState.value.placementMode)
    }

    // ---- Normal flow ---------------------------------------------------

    @Test
    fun `onCellTapped on own unit sets selected`() {
        val unit = own(2, 3)
        vm.onCellTapped(2, 3, listOf(unit))

        assertEquals(unit, vm.uiState.value.selected)
        verify(exactly = 0) { endpoint.sendMove(any(), any()) }
    }

    @Test
    fun `onCellTapped on empty cell without selection ignores tap`() {
        vm.onCellTapped(5, 5, emptyList())
        assertNull(vm.uiState.value.selected)
        verify(exactly = 0) { endpoint.sendMove(any(), any()) }
    }

    @Test
    fun `onCellTapped with selection on empty cell sends move and clears selection`() {
        val unit = own(2, 2)
        vm.onCellTapped(2, 2, listOf(unit))
        vm.onCellTapped(5, 5, listOf(unit))

        assertNull(vm.uiState.value.selected)
        verify {
            endpoint.sendMove(
                "test-room",
                Move(alice, UnitType.INFANTRY, 2, 2, 5, 5)
            )
        }
    }

    @Test
    fun `onCellTapped switches selection when tapping another own unit`() {
        val first = own(1, 1)
        val second = own(2, 2)

        vm.onCellTapped(1, 1, listOf(first, second))
        assertEquals(first, vm.uiState.value.selected)

        vm.onCellTapped(2, 2, listOf(first, second))
        assertEquals(second, vm.uiState.value.selected)

        verify(exactly = 0) { endpoint.sendMove(any(), any()) }
    }

    @Test
    fun `onCellTapped with selection on enemy sends move`() {
        val mine = own(2, 2)
        val theirs = enemy(3, 3)
        vm.onCellTapped(2, 2, listOf(mine, theirs))
        vm.onCellTapped(3, 3, listOf(mine, theirs))

        verify { endpoint.sendMove("test-room", Move(alice, UnitType.INFANTRY, 2, 2, 3, 3)) }
        assertNull(vm.uiState.value.selected)
    }

    @Test
    fun `sendMove clears lastError before sending`() {
        session.lastError.value = ErrorMessage(ErrorCode.INVALID_MOVE, "bla")
        val mine = own(1, 1)

        vm.onCellTapped(1, 1, listOf(mine))
        vm.onCellTapped(2, 2, listOf(mine))

        assertNull(session.lastError.value)
    }

    // ---- Placement-Modus ---------------------------------------------

    @Test
    fun `startPlacement sets placementMode and clears selection`() {
        // Erst was selektieren
        vm.onCellTapped(2, 2, listOf(own(2, 2)))
        // Dann placement starten
        vm.startPlacement(UnitType.ARCHER)
        assertEquals(UnitType.ARCHER, vm.uiState.value.placementMode)
        assertNull(vm.uiState.value.selected)
    }

    @Test
    fun `cancelPlacement resets placementMode`() {
        vm.startPlacement(UnitType.INFANTRY)
        vm.cancelPlacement()
        assertNull(vm.uiState.value.placementMode)
    }

    @Test
    fun `tap in placement mode sends buyUnit and exits mode`() {
        vm.startPlacement(UnitType.ARCHER)
        vm.onCellTapped(4, 5, emptyList())

        verify {
            endpoint.buyUnit(
                roomId = "test-room",
                playerName = alice,
                type = UnitType.ARCHER,
                x = 4,
                y = 5
            )
        }
        assertNull(vm.uiState.value.placementMode)
    }

    @Test
    fun `tap in placement mode does not send move even if a unit was previously selected`() {
        val unit = own(1, 1)
        vm.onCellTapped(1, 1, listOf(unit)) // selektiert
        vm.startPlacement(UnitType.CAVALRY) // selection wird gecancelt
        vm.onCellTapped(2, 2, listOf(unit))

        verify(exactly = 0) { endpoint.sendMove(any(), any()) }
        verify(exactly = 1) { endpoint.buyUnit(any(), any(), any(), any(), any()) }
    }

    // ---- tapToCell ---------------------------------------------------

    @Test
    fun `tapToCell delegates to GameScreenLogic`() {
        val result = vm.tapToCell(
            tapX = 100f, tapY = 100f,
            viewportSize = IntSize(200, 200)
        ) { _, _ -> 7 to 8 }
        assertEquals(7 to 8, result)
    }

    @Test
    fun `tapToCell returns null for zero viewport`() {
        val result = vm.tapToCell(100f, 100f, IntSize.Zero) { _, _ -> 5 to 5 }
        assertNull(result)
    }
}
