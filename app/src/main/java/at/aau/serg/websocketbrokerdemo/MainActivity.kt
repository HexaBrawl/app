package at.aau.serg.websocketbrokerdemo

import MyStomp
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import at.aau.serg.websocketbrokerdemo.audio.MusicManager
import at.aau.serg.websocketbrokerdemo.data.LocaleCache
import at.aau.serg.websocketbrokerdemo.data.LocaleHelper
import at.aau.serg.websocketbrokerdemo.data.SettingsRepository
import at.aau.serg.websocketbrokerdemo.ui.navigation.AppNavHost
import com.example.myapplication.R
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity(), Callbacks {

    lateinit var myStomp: MyStomp
    private val responseState = mutableStateOf("")

    /**
     * WICHTIG: Hier wird die Sprache angewandt, BEVOR die Activity richtig
     * hochgefahren ist. Compose & alle Resources nutzen dann die korrekte Locale.
     */
    override fun attachBaseContext(newBase: Context) {
        val lang = LocaleCache.get(newBase)
        val wrapped = LocaleHelper.updateLocale(newBase, lang)
        super.attachBaseContext(wrapped)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        myStomp = MyStomp(this)

        // Audio-System initialisieren
        MusicManager.init(applicationContext)

        // Persistierte Audio-Settings einmalig anwenden
        lifecycleScope.launch {
            val s = SettingsRepository(applicationContext).settings.first()
            MusicManager.applyMusicSettings(s.musicEnabled, s.musicVolume)
            MusicManager.applySfxSettings(s.sfxEnabled)
        }

        setContent {
            val navController = rememberNavController()
            AppNavHost(navController, myStomp, responseState)
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

    override fun onResponse(res: String) {
        responseState.value = res
    }

    override fun onDestroy() {
        super.onDestroy()
        myStomp.disconnect()
        MusicManager.release()
    }
}