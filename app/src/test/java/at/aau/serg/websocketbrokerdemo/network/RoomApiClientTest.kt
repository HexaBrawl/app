package at.aau.serg.websocketbrokerdemo.network

import at.aau.serg.websocketbrokerdemo.data.serverside.GameMode
import at.aau.serg.websocketbrokerdemo.data.serverside.Player
import at.aau.serg.websocketbrokerdemo.data.serverside.RoomDTO
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RoomApiClientTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var client: RoomApiClient
    private val gson = Gson()

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        client = RoomApiClient(
            baseUrl = mockWebServer.url("/").toString(),
            gson = gson
        )
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `createRoom returns RoomDTO on success`() = runBlocking {
        val mockRoom = RoomDTO("ABC123", GameMode.DUAL_VALLEY, emptyList())
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(gson.toJson(mockRoom))
        )

        val result = client.createRoom(GameMode.DUAL_VALLEY)

        assertNotNull(result)
        assertEquals("ABC123", result?.joinCode)
        assertEquals(GameMode.DUAL_VALLEY, result?.mode)
    }

    @Test
    fun `createRoom returns null on HTTP error`() = runBlocking {
        mockWebServer.enqueue(MockResponse().setResponseCode(500))

        val result = client.createRoom(GameMode.DUAL_VALLEY)

        assertNull(result)
    }

    @Test
    fun `findByCode returns RoomDTO on success`() = runBlocking {
        val mockRoom = RoomDTO("XYZ789", GameMode.TRIAD_OUTPOST, listOf(Player(name = "Host")))
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(gson.toJson(mockRoom))
        )

        val result = client.findByCode("XYZ789")

        assertNotNull(result)
        assertEquals("XYZ789", result?.joinCode)
        assertEquals(1, result?.players?.size)
    }

    @Test
    fun `findByCode returns null on 404`() = runBlocking {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))

        val result = client.findByCode("NONE")

        assertNull(result)
    }

    @Test
    fun `findByCode returns null on network error`() = runBlocking {
        mockWebServer.shutdown()

        val result = client.findByCode("FAIL")

        assertNull(result)
    }

    @Test
    fun `Gson smoke test with real server JSON`() {
        val json = """
            {
                "joinCode": "CODE01",
                "mode": "BATTLEFIELD_PEAKS",
                "players": [
                    { "name": "Alice", "color": "RED", "gold": 100 }
                ]
            }
        """.trimIndent()

        val room = gson.fromJson(json, RoomDTO::class.java)

        assertNotNull(room)
        assertEquals("CODE01", room.joinCode)
        assertEquals(GameMode.BATTLEFIELD_PEAKS, room.mode)
        assertEquals(1, room.players.size)
        assertEquals("Alice", room.players[0].name)
    }
}
