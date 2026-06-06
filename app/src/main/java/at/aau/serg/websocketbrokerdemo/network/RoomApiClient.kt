package at.aau.serg.websocketbrokerdemo.network

import android.util.Log
import at.aau.serg.websocketbrokerdemo.data.serverside.GameMode
import at.aau.serg.websocketbrokerdemo.data.serverside.RoomDTO
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * REST-Client fuer die Room-Endpoints des Servers.
 *
 * Nutzt OkHttp direkt -- der Client ist bereits transitiv ueber Krossbow
 * vorhanden, sodass keine zusaetzliche Dependency (Retrofit etc.)
 * eingefuehrt werden muss.
 *
 * Beide Methoden sind suspend und laufen auf [Dispatchers.IO]; Aufrufer
 * in ViewModels starten sie aus `viewModelScope.launch`. Bei Netzwerk-
 * fehlern oder unerwartetem Status-Code liefern sie `null`, sodass das
 * aufrufende [at.aau.serg.websocketbrokerdemo.ui.lobby_modes.LobbyRoomLogic]
 * die Faelle einheitlich auf [at.aau.serg.websocketbrokerdemo.ui.lobby_modes.LobbyEffect.ShowError]
 * mappen kann.
 */
class RoomApiClient(
    private val baseUrl: String = DEFAULT_BASE_URL,
    private val client: OkHttpClient = OkHttpClient(),
    private val gson: Gson = Gson()
) {

    /**
     * POST /api/rooms?mode={mode}
     * Erstellt einen neuen Raum mit dem angegebenen [mode].
     *
     * @return Den erstellten Raum als [RoomDTO] oder null bei Netzwerk-
     *         oder Server-Fehler.
     */
    suspend fun createRoom(mode: GameMode): RoomDTO? = withContext(Dispatchers.IO) {
        runCatching {
            val request = Request.Builder()
                .url("$baseUrl/api/rooms?mode=${mode.name}")
                .post(ByteArray(0).toRequestBody(JSON_MEDIA_TYPE))
                .build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.w(TAG, "createRoom HTTP ${response.code}")
                    return@use null
                }
                gson.fromJson(response.body?.string(), RoomDTO::class.java)
            }
        }.onFailure { Log.e(TAG, "createRoom failed", it) }.getOrNull()
    }

    /**
     * GET /api/rooms/by-code/{joinCode}
     * Sucht einen Raum ueber seinen 6-Zeichen-joinCode.
     *
     * @return Den gefundenen Raum als [RoomDTO] oder null wenn 404 bzw.
     *         bei Netzwerkfehler.
     */
    suspend fun findByCode(joinCode: String): RoomDTO? = withContext(Dispatchers.IO) {
        runCatching {
            val request = Request.Builder()
                .url("$baseUrl/api/rooms/by-code/$joinCode")
                .get()
                .build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.w(TAG, "findByCode HTTP ${response.code} for code=$joinCode")
                    return@use null
                }
                gson.fromJson(response.body?.string(), RoomDTO::class.java)
            }
        }.onFailure { Log.e(TAG, "findByCode failed for code=$joinCode", it) }.getOrNull()
    }

    companion object {
        private const val TAG = "RoomApiClient"
        private val JSON_MEDIA_TYPE = "application/json".toMediaTypeOrNull()

        /**
         * Azure-Deployment-URL fuer die REST-Endpoints. MUSS synchron
         * gehalten werden mit der WebSocket-URL in [Stomp]. Idealerweise
         * spaeter beide aus einer zentralen Config ziehen.
         */
        const val DEFAULT_BASE_URL =
            "https://hexabrawl-server-fkcdhrf9avdbebh5.germanywestcentral-01.azurewebsites.net"
    }
}