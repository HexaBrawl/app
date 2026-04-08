package at.aau.serg.websocketbrokerdemo

import MyStomp
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.compose.rememberNavController
import at.aau.serg.websocketbrokerdemo.ui.navigation.AppNavHost
import com.example.myapplication.R

class MainActivity : ComponentActivity(), Callbacks {
    lateinit var myStomp: MyStomp   //STOMP-Client-Instanz
    private val responseState = mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Initialisierte STOMP-Client mit Callback-Interface
        myStomp = MyStomp(this)

        //Compose-Content setzen
        setContent {
            val navController = rememberNavController()
            AppNavHost(navController, myStomp, responseState)
        }
    }

    override fun onResponse(res: String) {
        responseState.value = res
    }

    override fun onDestroy() {
        super.onDestroy()
        myStomp.disconnect()
    }


}

