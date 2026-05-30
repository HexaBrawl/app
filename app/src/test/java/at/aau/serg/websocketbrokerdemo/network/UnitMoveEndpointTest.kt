package at.aau.serg.websocketbrokerdemo.network

import at.aau.serg.websocketbrokerdemo.data.serverside.ErrorCode
import at.aau.serg.websocketbrokerdemo.data.serverside.ErrorMessage
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
    fun `sendMove sends serialized Move as JSON`() {

        val move = Move(
            player = "Alice",
            fromX = 1,
            fromY = 2,
            toX = 3,
            toY = 4
        )

        val slot = slot<String>()

        every { stomp.sendJson("/app/rooms/game1/move", capture(slot)) } just Runs

        endpoint.sendMove("game1", move)

        verify {
            stomp.sendJson(eq("/app/rooms/game1/move"), any())
        }

        assert(slot.isCaptured)
        assert(slot.captured.contains("\"player\":\"Alice\""))
    }

    @Test
    fun `joinGame sends player name as text`() {

        endpoint.joinGame("game1", "Max")

        verify {
            stomp.sendText("/app/rooms/game1/join", "Max")
        }
    }

    @Test
    fun `requestInitialState sends empty init message`() {

        endpoint.requestInitialState("game1")

        verify {
            stomp.sendText("/app/rooms/game1/init/", "")
        }
    }

    @Test
    fun `subscribeToGameState parses GameState correctly`() {

        val slot = slot<(String) -> Unit>()

        every {
            stomp.subscribe("/topic/rooms/game1/state", capture(slot))
        } returns mockk(relaxed = true)

        var received: GameState? = null

        endpoint.subscribeToGameState("game1") {
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

    @Test
    fun `subscribeToErrors parses ErrorMessage correctly`() {

        val slot = slot<(String) -> Unit>()

        every {
            stomp.subscribe("/user/queue/errors", capture(slot))
        } returns mockk(relaxed = true)

        var received: ErrorMessage? = null

        endpoint.subscribeToErrors { received = it }

        val json = """{"errorCode":"NOT_YOUR_TURN","message":"Not your turn!"}"""
        slot.captured.invoke(json)

        assert(received?.errorCode == ErrorCode.NOT_YOUR_TURN)
        assert(received?.message == "Not your turn!")
    }
}
