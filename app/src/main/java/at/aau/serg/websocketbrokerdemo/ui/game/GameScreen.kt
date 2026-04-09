package at.aau.serg.websocketbrokerdemo.ui.game

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import MyStomp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import at.aau.serg.websocketbrokerdemo.grid.GridShape
import at.aau.serg.websocketbrokerdemo.grid.HexGrid
import at.aau.serg.websocketbrokerdemo.grid.UnitData

@Composable
fun GameScreen(myStomp: MyStomp, responseState: State<String>) {
    Column(modifier = Modifier.fillMaxSize()) {

        // HexGrid Composable verwenden
        HexGrid(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 32.dp),
            units = listOf(
                UnitData(3, 4, "Player 1"),
                UnitData(5, 2, "Player 2")
            ),
            shape = GridShape.RECTANGLE,
            onHexClicked = { x, y ->
                println("Hexagon angeklickt: $x, $y")
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text("Server Response: ${responseState.value}")

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly)
            {
                Button(onClick = { myStomp.connect() }) { Text("Connect") }
                Button(onClick = { myStomp.sendHello() }) { Text("Send Hello") }
                Button(onClick = { myStomp.sendJson() }) { Text("Send JSON") }
            }
        }
    }
}
