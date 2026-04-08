package at.aau.serg.websocketbrokerdemo.ui.game

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import MyStomp
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.runtime.State

@Composable
fun GameScreen(myStomp: MyStomp, responseState: State<String>) {
        Column {
                Text("Server Response: ${responseState.value}")
                Button(onClick = { myStomp.connect() }) { Text("Connect") }
                Button(onClick = { myStomp.sendHello() }) { Text("Send Hello") }
                Button(onClick = { myStomp.sendJson() }) { Text("Send JSON") }
        }
}