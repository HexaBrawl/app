package at.aau.serg.websocketbrokerdemo

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import at.aau.serg.websocketbrokerdemo.audio.MusicManager
import at.aau.serg.websocketbrokerdemo.data.LocaleCache
import at.aau.serg.websocketbrokerdemo.data.LocaleHelper
import at.aau.serg.websocketbrokerdemo.data.SettingsRepository
import at.aau.serg.websocketbrokerdemo.data.settingsDataStore
import at.aau.serg.websocketbrokerdemo.network.GameSession
import at.aau.serg.websocketbrokerdemo.network.Stomp
import at.aau.serg.websocketbrokerdemo.network.UnitMoveEndpoint
import at.aau.serg.websocketbrokerdemo.ui.navigation.AppNavHost
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val stomp = Stomp()
    private val endpoint = UnitMoveEndpoint(stomp)
    private val session = GameSession(endpoint)

    override fun attachBaseContext(newBase: Context) {
        val lang = LocaleCache.get(newBase)
        val wrapped = LocaleHelper.updateLocale(newBase, lang)
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

        // Connect STOMP once for the whole app session; then wire the two
        // server-side streams into the shared GameSession state.
        lifecycleScope.launch {
            try {
                stomp.connect()
                endpoint.subscribeToGameState(session.roomId.value) { state ->
                    session.gameState.value = state
                    session.gameStateReceivedCount.intValue += 1
                }
                endpoint.subscribeToErrors { err ->
                    Log.w(TAG, "Server error: ${err.errorCode} - ${err.message}")
                    session.lastError.value = err
                    session.errorReceivedCount.intValue += 1
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
        super.onDestroy()
        stomp.disconnect()
        MusicManager.release()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
