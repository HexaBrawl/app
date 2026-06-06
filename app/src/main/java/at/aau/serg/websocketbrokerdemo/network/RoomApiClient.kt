package at.aau.serg.websocketbrokerdemo.network

import at.aau.serg.websocketbrokerdemo.data.serverside.GameMode
import at.aau.serg.websocketbrokerdemo.data.serverside.RoomDTO
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

/**
 * REST-Client fuer die Room-Endpoints des Servers.
 *
 * Nutzt OkHttp direkt -- der Client ist bereits transitiv ueber Krossbow
 * vorhanden, sodass keine zusaetzliche Dependency (Retrofit etc.)
 * eingefuehrt werden muss.
 *
 * Beide Methoden sind suspend und laufen auf [Dispatchers.IO].
 */
class RoomApiClient(
    private val baseUrl: String = DEFAULT_BASE_URL,
    private val client: OkHttpClient = OkHttpClient(),
    private val gson: Gson = Gson()
) {
    /**
     * Erstellt einen Raum (POST /api/rooms?mode=...).
     */
    suspend fun createRoom(mode: GameMode): RoomDTO? = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$baseUrl/api/rooms?mode=${mode.name}")
            .post("".toRequestBody())
            .build()
        execute(request)
    }

    /**
     * Sucht einen Raum (GET /api/rooms/by-code/{code}).
     */
    suspend fun findByCode(code: String): RoomDTO? = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$baseUrl/api/rooms/by-code/$code")
            .get()
            .build()
        execute(request)
    }

    private fun execute(request: Request): RoomDTO? {
        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val json = response.body?.string()
                    gson.fromJson(json, RoomDTO::class.java)
                } else {
                    null
                }
            }
        } catch (_: IOException) {
            null
        }
    }

    companion object {
        /**
         * TODO: Später in eine zentrale Config auslagern.
         * Muss synchron zur URL in Stomp.kt gehalten werden.
         */
        const val DEFAULT_BASE_URL = "https://hexabrawl-server-fkcdhrf9avdbebh5.germanywestcentral-01.azurewebsites.net"
    }
}
