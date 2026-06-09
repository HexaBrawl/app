package at.aau.serg.websocketbrokerdemo.network

import android.util.Log
import at.aau.serg.websocketbrokerdemo.data.serverside.ErrorCode
import at.aau.serg.websocketbrokerdemo.data.serverside.ErrorMessage
import at.aau.serg.websocketbrokerdemo.data.serverside.GameState
import at.aau.serg.websocketbrokerdemo.data.serverside.GameStatus
import at.aau.serg.websocketbrokerdemo.data.serverside.Move
import at.aau.serg.websocketbrokerdemo.data.serverside.PlayerColor
import at.aau.serg.websocketbrokerdemo.data.serverside.UnitType
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Job
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests fuer UnitMoveEndpoint.
 *
 * Stomp wird vollstaendig gemockt -- wir verifizieren dass die richtige
 * STOMP-Methode (sendJson) mit dem korrekt serialisierten JSON-Payload
 * aufgerufen wird. Gson wird real verwendet, damit Aenderungen am DTO-
 * Format hier auffallen wuerden.
 *
 * Log wird statisch gemockt, damit Android-Log-Aufrufe im Unit-Test
 * nicht crashen.
 */
class UnitMoveEndpointTest {

    private lateinit var stomp: Stomp
    private lateinit var endpoint: UnitMoveEndpoint

    @BeforeEach
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.d(any(), any<String>()) } returns 0
        every { Log.e(any(), any<String>(), any()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0
        every { Log.i(any(), any<String>()) } returns 0

        stomp = mockk(relaxed = true)
        justRun { stomp.sendJson(any(), any()) }
        endpoint = UnitMoveEndpoint(stomp)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    // ---- sendMove ------------------------------------------------------

    @Test
    fun `sendMove sends correctly serialized JSON to room topic`() {
        val move = Move(
            player = "Alice",
            type = UnitType.INFANTRY,
            fromX = 2, fromY = 2,
            toX = 5, toY = 5
        )

        endpoint.sendMove("game1", move)

        verify {
            stomp.sendJson(
                "/app/rooms/game1/move",
                """{"player":"Alice","type":"INFANTRY","fromX":2,"fromY":2,"toX":5,"toY":5}"""
            )
        }
    }

    @Test
    fun `sendMove uses the room id provided`() {
        val move = Move(player = "Alice", type = UnitType.CAVALRY)

        endpoint.sendMove("other-room", move)

        verify {
            stomp.sendJson(
                destination = match { it == "/app/rooms/other-room/move" },
                json = any()
            )
        }
    }

    // ---- joinGame ------------------------------------------------------

    @Test
    fun `joinGame sends JSON with name and color`() {
        endpoint.joinGame("game1", "Max", PlayerColor.RED)

        verify {
            stomp.sendJson(
                "/app/rooms/game1/join",
                """{"name":"Max","color":"RED"}"""
            )
        }
    }

    @Test
    fun `joinGame serializes all PlayerColor values correctly`() {
        endpoint.joinGame("g", "A", PlayerColor.GREEN)
        endpoint.joinGame("g", "B", PlayerColor.YELLOW)
        endpoint.joinGame("g", "C", PlayerColor.BLUE)

        verify { stomp.sendJson(any(), """{"name":"A","color":"GREEN"}""") }
        verify { stomp.sendJson(any(), """{"name":"B","color":"YELLOW"}""") }
        verify { stomp.sendJson(any(), """{"name":"C","color":"BLUE"}""") }
    }

    @Test
    fun `joinGame without color sends null in JSON so server picks a free color`() {
        endpoint.joinGame(roomId = "game1", playerName = "Max")

        verify {
            stomp.sendJson(
                "/app/rooms/game1/join",
                """{"name":"Max","color":null}"""
            )
        }
    }

    @Test
    fun `joinGame with explicit null color sends null in JSON`() {
        endpoint.joinGame(roomId = "game1", playerName = "Max", color = null)

        verify {
            stomp.sendJson(
                "/app/rooms/game1/join",
                """{"name":"Max","color":null}"""
            )
        }
    }

    // ---- claimCheatGift ------------------------------------------------

    @Test
    fun `claimCheatGift sends JSON with playerName and delta`() {
        endpoint.claimCheatGift("game1", "Alice", 7)

        verify {
            stomp.sendJson(
                "/app/rooms/game1/cheat/claim-gift",
                """{"playerName":"Alice","delta":7}"""
            )
        }
    }

    @Test
    fun `claimCheatGift handles negative delta`() {
        endpoint.claimCheatGift("game1", "Alice", -5)

        verify {
            stomp.sendJson(
                "/app/rooms/game1/cheat/claim-gift",
                """{"playerName":"Alice","delta":-5}"""
            )
        }
    }

    @Test
    fun `claimCheatGift uses correct room id`() {
        endpoint.claimCheatGift("battlefield-7", "Bob", 3)

        verify {
            stomp.sendJson(
                destination = "/app/rooms/battlefield-7/cheat/claim-gift",
                json = any()
            )
        }
    }

    // ---- respondToCheatGift --------------------------------------------

    @Test
    fun `respondToCheatGift with accept=true sends correct JSON`() {
        endpoint.respondToCheatGift("game1", "Bob", true)

        verify {
            stomp.sendJson(
                "/app/rooms/game1/cheat/respond-steal",
                """{"playerName":"Bob","accept":true}"""
            )
        }
    }

    @Test
    fun `respondToCheatGift with accept=false sends correct JSON`() {
        endpoint.respondToCheatGift("game1", "Bob", false)

        verify {
            stomp.sendJson(
                "/app/rooms/game1/cheat/respond-steal",
                """{"playerName":"Bob","accept":false}"""
            )
        }
    }

    @Test
    fun `buyFarm sends correct JSON`() {
        endpoint.buyFarm("game1", "Alice")

        verify {
            stomp.sendJson(
                "/app/rooms/game1/buy-farm",
                """{"playerName":"Alice"}"""
            )
        }
    }

    @Test
    fun `buyFarm uses correct room id`() {
        endpoint.buyFarm("room-77", "Bob")

        verify {
            stomp.sendJson(
                destination = "/app/rooms/room-77/buy-farm",
                json = any()
            )
        }
    }

    @Test
    fun `buyUnit sends correct JSON with all fields`() {
        endpoint.buyUnit("game1", "Alice", UnitType.ARCHER, 3, 4)

        verify {
            stomp.sendJson(
                "/app/rooms/game1/buy-unit",
                """{"playerName":"Alice","type":"ARCHER","x":3,"y":4}"""
            )
        }
    }

    @Test
    fun `buyUnit serializes UnitType correctly`() {
        endpoint.buyUnit("g", "A", UnitType.INFANTRY, 0, 0)
        endpoint.buyUnit("g", "B", UnitType.CAVALRY, 1, 1)
        endpoint.buyUnit("g", "C", UnitType.ARCHER, 2, 2)

        verify { stomp.sendJson(any(), """{"playerName":"A","type":"INFANTRY","x":0,"y":0}""") }
        verify { stomp.sendJson(any(), """{"playerName":"B","type":"CAVALRY","x":1,"y":1}""") }
        verify { stomp.sendJson(any(), """{"playerName":"C","type":"ARCHER","x":2,"y":2}""") }
    }

    @Test
    fun `buyUnit uses correct room id`() {
        endpoint.buyUnit("battle-9", "Alice", UnitType.INFANTRY, 7, 8)

        verify {
            stomp.sendJson(
                destination = "/app/rooms/battle-9/buy-unit",
                json = any()
            )
        }
    }

    @Test
    fun `endTurn sends correct JSON`() {
        endpoint.endTurn("game1", "Alice")

        verify {
            stomp.sendJson(
                "/app/rooms/game1/end-turn",
                """{"playerName":"Alice"}"""
            )
        }
    }

    @Test
    fun `endTurn uses correct room id`() {
        endpoint.endTurn("arena-55", "Bob")

        verify {
            stomp.sendJson(
                destination = "/app/rooms/arena-55/end-turn",
                json = any()
            )
        }
    }




    // ---- subscribeToGameState ------------------------------------------

    @Test
    fun `subscribeToGameState forwards parsed GameState to callback`() {
        val json = """{"players":[],"units":[],"currentTurn":null,"status":"WAITING_FOR_PLAYERS","pendingGift":null}"""

        every { stomp.subscribe(any(), any()) } answers {
            val callback = secondArg<(String) -> Unit>()
            callback(json)
            mockk<Job>(relaxed = true)
        }

        var received: GameState? = null
        endpoint.subscribeToGameState("game1") { state -> received = state }

        assertNotNull(received)
        assertEquals(GameStatus.WAITING_FOR_PLAYERS, received?.status)
        assertEquals(0, received?.players?.size)
        assertEquals(0, received?.units?.size)
    }

    @Test
    fun `subscribeToGameState uses correct topic`() {
        every { stomp.subscribe(any(), any()) } returns mockk<Job>(relaxed = true)

        endpoint.subscribeToGameState("my-room") { }

        verify {
            stomp.subscribe(
                topic = "/topic/rooms/my-room/state",
                onMessage = any()
            )
        }
    }

    @Test
    fun `subscribeToGameState ignores malformed JSON gracefully`() {
        every { stomp.subscribe(any(), any()) } answers {
            val callback = secondArg<(String) -> Unit>()
            callback("not valid json at all")
            mockk<Job>(relaxed = true)
        }

        var received: GameState? = null
        // Sollte nicht crashen
        endpoint.subscribeToGameState("game1") { state -> received = state }

        // Callback wurde nicht aufgerufen weil parsing fehlgeschlagen
        assertEquals(null, received)
    }

    // ---- subscribeToErrors ---------------------------------------------

    @Test
    fun `subscribeToErrors forwards parsed ErrorMessage to callback`() {
        val json = """{"errorCode":"INVALID_MOVE","message":"You cant move there"}"""

        every { stomp.subscribe(any(), any()) } answers {
            val callback = secondArg<(String) -> Unit>()
            callback(json)
            mockk<Job>(relaxed = true)
        }

        var received: ErrorMessage? = null
        endpoint.subscribeToErrors { err -> received = err }

        assertNotNull(received)
        assertEquals(ErrorCode.INVALID_MOVE, received?.errorCode)
        assertEquals("You cant move there", received?.message)
    }

    @Test
    fun `subscribeToErrors uses correct topic`() {
        every { stomp.subscribe(any(), any()) } returns mockk<Job>(relaxed = true)

        endpoint.subscribeToErrors { }

        verify {
            stomp.subscribe(
                topic = "/user/queue/errors",
                onMessage = any()
            )
        }
    }

    @Test
    fun `subscribeToErrors ignores malformed JSON gracefully`() {
        every { stomp.subscribe(any(), any()) } answers {
            val callback = secondArg<(String) -> Unit>()
            callback("garbage payload")
            mockk<Job>(relaxed = true)
        }

        var received: ErrorMessage? = null
        endpoint.subscribeToErrors { err -> received = err }

        assertEquals(null, received)
    }
}