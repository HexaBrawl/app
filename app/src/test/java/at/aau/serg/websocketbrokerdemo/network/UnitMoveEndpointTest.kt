package at.aau.serg.websocketbrokerdemo.network

import at.aau.serg.websocketbrokerdemo.data.serverside.GameState
import at.aau.serg.websocketbrokerdemo.data.serverside.Move
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UnitMoveEndpointTest {

    private lateinit var stomp: Stomp
    private lateinit var endpoint: UnitMoveEndpoint

    @BeforeEach
    fun setup() {
        stomp = mockk(relaxed = true)
        endpoint = UnitMoveEndpoint(stomp)
    }

    @Test
    fun `sendMove sends serialized Move`() {

        val move = Move(
            player = "Alice",
            fromX = 1,
            fromY = 2,
            toX = 3,
            toY = 4
        )

        val slot = slot<String>()

        every { stomp.send("/app/move", capture(slot)) } just Runs

        endpoint.sendMove(move)

        verify {
            stomp.send(eq("/app/move"), any())
        }

        assert(slot.isCaptured)
        assert(slot.captured.contains("\"player\":\"Alice\""))
    }

    @Test
    fun `joinGame sends player name`() {

        endpoint.joinGame("Max")

        verify {
            stomp.send("/app/join", "Max")
        }
    }

    @Test
    fun `requestInitialState sends empty init message`() {

        endpoint.requestInitialState()

        verify {
            stomp.send("/app/init", "")
        }
    }

    @Test
    fun `subscribeToGameState parses GameState correctly`() {

        val slot = slot<(String) -> Unit>()

        every {
            stomp.subscribe("/topic/game", capture(slot))
        } returns mockk(relaxed = true)

        var received: GameState? = null

        endpoint.subscribeToGameState {
            received = it
        }

        val json = """
            {
              "players": [],
              "units": [],
              "currentTurn": "Alice",
              "status": "WAITING_FOR_PLAYERS"
            }
        """.trimIndent()

        slot.captured.invoke(json)

        assert(received != null)
        assert(received?.currentTurn == "Alice")
        assert(received?.players?.isEmpty() == true)
    }
}