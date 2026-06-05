package at.aau.serg.websocketbrokerdemo.ui.game.tophud

import androidx.compose.runtime.mutableStateOf
import at.aau.serg.websocketbrokerdemo.network.GameSession
import at.aau.serg.websocketbrokerdemo.network.UnitMoveEndpoint
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.random.Random

/**
 * Tests fuer CheatGiftViewModel.
 */
class CheatGiftViewModelTest {

    private lateinit var endpoint: UnitMoveEndpoint
    private lateinit var session: GameSession
    private lateinit var vm: CheatGiftViewModel

    @BeforeEach
    fun setUp() {
        endpoint = mockk(relaxed = true)
        justRun { endpoint.claimCheatGift(any(), any(), any()) }
        justRun { endpoint.respondToCheatGift(any(), any(), any()) }
        session = GameSession(
            endpoint = endpoint,
            activeRoomId = mutableStateOf("test-room"),
            localPlayerName = mutableStateOf("Alice")
        )
        // Seed wo wir das Delta vorhersagen koennen
        vm = CheatGiftViewModel(session, random = Random(42))
    }

    // ---- Initialer State -----------------------------------------------

    @Test
    fun `initial click count is zero`() {
        assertEquals(0, vm.state.value.clickCount)
    }

    @Test
    fun `initial hasResponded is false`() {
        assertFalse(vm.state.value.hasResponded)
    }

    // ---- onGiftClick ---------------------------------------------------

    @Test
    fun `onGiftClick increments counter until 5`() {
        vm.onGiftClick("Alice")
        assertEquals(1, vm.state.value.clickCount)

        vm.onGiftClick("Alice")
        vm.onGiftClick("Alice")
        vm.onGiftClick("Alice")
        assertEquals(4, vm.state.value.clickCount)

        verify(exactly = 0) { endpoint.claimCheatGift(any(), any(), any()) }
    }

    @Test
    fun `onGiftClick triggers claimCheatGift on fifth click`() {
        repeat(5) { vm.onGiftClick("Alice") }

        verify(exactly = 1) {
            endpoint.claimCheatGift(
                roomId = "test-room",
                playerName = "Alice",
                delta = any()
            )
        }
    }

    @Test
    fun `onGiftClick resets counter after triggering`() {
        repeat(5) { vm.onGiftClick("Alice") }
        assertEquals(0, vm.state.value.clickCount)
    }

    @Test
    fun `sent delta is within valid range`() {
        var capturedDelta = Int.MIN_VALUE
        every {
            endpoint.claimCheatGift(any(), any(), any())
        } answers {
            capturedDelta = thirdArg()
        }

        repeat(5) { vm.onGiftClick("Alice") }

        assertTrue(capturedDelta in CheatGiftLogic.MIN_DELTA..CheatGiftLogic.MAX_DELTA)
    }

    // ---- onStealAccept --------------------------------------------------

    @Test
    fun `onStealAccept sends respondToCheatGift with accept=true`() {
        vm.onStealAccept("Bob")
        verify {
            endpoint.respondToCheatGift(
                roomId = "test-room",
                playerName = "Bob",
                accept = true
            )
        }
    }

    @Test
    fun `onStealAccept sets hasResponded`() {
        vm.onStealAccept("Bob")
        assertTrue(vm.state.value.hasResponded)
    }

    @Test
    fun `onStealAccept is ignored when already responded`() {
        vm.onStealAccept("Bob")
        vm.onStealAccept("Bob")
        verify(exactly = 1) { endpoint.respondToCheatGift(any(), any(), any()) }
    }

    // ---- onStealDecline -------------------------------------------------

    @Test
    fun `onStealDecline sends respondToCheatGift with accept=false`() {
        vm.onStealDecline("Bob")
        verify {
            endpoint.respondToCheatGift(
                roomId = "test-room",
                playerName = "Bob",
                accept = false
            )
        }
    }

    @Test
    fun `onStealDecline sets hasResponded`() {
        vm.onStealDecline("Bob")
        assertTrue(vm.state.value.hasResponded)
    }

    @Test
    fun `onStealDecline is ignored when already responded`() {
        vm.onStealAccept("Bob")
        vm.onStealDecline("Bob")
        verify(exactly = 1) { endpoint.respondToCheatGift(any(), any(), any()) }
    }

    // ---- onPendingGiftCleared -----------------------------------------

    @Test
    fun `onPendingGiftCleared resets hasResponded`() {
        vm.onStealAccept("Bob")
        assertTrue(vm.state.value.hasResponded)

        vm.onPendingGiftCleared()
        assertFalse(vm.state.value.hasResponded)
    }

    @Test
    fun `onPendingGiftCleared is no-op when not yet responded`() {
        vm.onPendingGiftCleared()
        assertFalse(vm.state.value.hasResponded)
    }
}
