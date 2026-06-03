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

class MainActivity : ComponentActivity() {

    private val stomp = Stomp()
    private val endpoint = UnitMoveEndpoint(stomp)
    private val session = GameSession(endpoint)

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
        super.onDestroy()
        stomp.disconnect()
        MusicManager.release()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
