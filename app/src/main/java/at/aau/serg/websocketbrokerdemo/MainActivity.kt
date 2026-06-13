package at.aau.serg.websocketbrokerdemo

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import at.aau.serg.websocketbrokerdemo.audio.MusicManager
import at.aau.serg.websocketbrokerdemo.data.LanguageCache
import at.aau.serg.websocketbrokerdemo.data.LanguageHelper
import at.aau.serg.websocketbrokerdemo.data.SettingsRepository
import at.aau.serg.websocketbrokerdemo.data.settingsDataStore
import at.aau.serg.websocketbrokerdemo.network.GameSession
import at.aau.serg.websocketbrokerdemo.network.Stomp
import at.aau.serg.websocketbrokerdemo.network.UnitMoveEndpoint
import at.aau.serg.websocketbrokerdemo.ui.navigation.AppNavHost
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout

class MainActivity : ComponentActivity() {

    private val stomp = Stomp()
    private val endpoint = UnitMoveEndpoint(stomp)
    private val session = GameSession(endpoint, connectionState = stomp.connectionState)

    override fun attachBaseContext(newBase: Context) {
        val lang = LanguageCache.get(newBase)
        val wrapped = LanguageHelper.updateLocale(newBase, lang)
        super.attachBaseContext(wrapped)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MusicManager.init(applicationContext)

        lifecycleScope.launch {
            val s = SettingsRepository(
                applicationContext.settingsDataStore,
                applicationContext
            ).settings.first()
            MusicManager.applyMusicSettings(s.musicEnabled, s.musicVolume)
            MusicManager.applySfxSettings(s.sfxEnabled)
        }

        // Auto-/reconnect-Hook: sobald die Stomp-interne Reconnect-Loop
        // den WebSocket wieder aufgebaut hat, schicken wir den /reconnect-
        // Application-Call mit der gespeicherten Spieler-Identitaet, sodass
        // der Server den Spieler wieder seinem Slot zuordnet. Wird nur
        // geschickt, wenn aktuell ueberhaupt ein Match laeuft (roomId
        // gesetzt) — in der Lobby ist nichts zu rejoinen.
        stomp.onReconnected {
            val repo = session.sessionRepository
            val roomId = repo.roomId
            val playerName = repo.playerName
            val joinCode = repo.joinCode
            if (!roomId.isNullOrBlank() && !playerName.isNullOrBlank() && !joinCode.isNullOrBlank()) {
                endpoint.reconnect(roomId, playerName, joinCode)
            }
        }

        // Connect STOMP once for the whole app session; then wire the error stream.
        lifecycleScope.launch {
            try {
                stomp.connect()
                endpoint.subscribeToErrors { err ->
                    Log.w(TAG, "Server error: ${err.errorCode} - ${err.message}")
                    session.lastError.value = err
                }
            } catch (e: Exception) {
                Log.e(TAG, "STOMP connect failed", e)
            }
        }

        setContent {
            val navController = rememberNavController()
            AppNavHost(navController, session)
        }
    }

    override fun onPause() {
        super.onPause()
        MusicManager.pause()
    }

    override fun onResume() {
        super.onResume()
        MusicManager.resume()
    }

    override fun onDestroy() {
        // /leave nur bei "echtem" Verlassen senden — also wenn der User
        // die App weggeswiped hat oder explizit aus dem Spiel raus ist
        // (isFinishing). Bei Hintergrund / Display-Off greift die Server-
        // Grace-Period, da soll NICHT geleaved werden.
        //
        // runBlocking + withTimeout, damit der STOMP-Frame zuverlaessig
        // raus geht, bevor der Process stirbt. 500ms ist eine sichere
        // Obergrenze (deutlich unter dem ANR-Limit von 5s und schon
        // grosszuegig fuer einen einzelnen Send).
        if (isFinishing) {
            val repo = session.sessionRepository
            val roomId = repo.roomId
            val playerName = repo.playerName
            if (!roomId.isNullOrBlank() && !playerName.isNullOrBlank()) {
                try {
                    runBlocking {
                        withTimeout(LEAVE_TIMEOUT_MS) {
                            endpoint.leaveGameAwait(roomId, playerName)
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "leaveGame on destroy failed/timed out", e)
                }
                repo.clear()
            }
        }
        super.onDestroy()
        stomp.disconnect()
        MusicManager.release()
    }

    companion object {
        private const val TAG = "MainActivity"

        /**
         * Timeout fuer den /leave-Send-Frame in onDestroy. Klein genug
         * um keinen ANR zu riskieren (Main-Thread blockiert), gross
         * genug fuer einen Single-Frame-Send auf einer warmen WebSocket.
         */
        private const val LEAVE_TIMEOUT_MS = 500L
    }
}
