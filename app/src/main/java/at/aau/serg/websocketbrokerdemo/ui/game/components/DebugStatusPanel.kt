package at.aau.serg.websocketbrokerdemo.ui.game.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import at.aau.serg.websocketbrokerdemo.data.serverside.ErrorMessage
import at.aau.serg.websocketbrokerdemo.data.serverside.GameState
import at.aau.serg.websocketbrokerdemo.data.serverside.GameUnit

/**
 * Debug-Statusleiste am unteren Bildschirmrand.
 *
 * Wird nur eingeblendet wenn DEBUG_HUD aktiv ist. Zeigt Status/Turn,
 * Last-Tap, Last-Move, Unit-Liste und Fehler an -- nuetzlich beim
 * Debuggen der Server-Kommunikation, nicht fuer Endnutzer gedacht.
 */
@Composable
fun DebugStatusPanel(
    localName: String?,
    gameState: GameState?,
    units: List<GameUnit>,
    lastTap: String,
    lastMove: String,
    lastError: ErrorMessage?,
    selected: GameUnit?,
    onClearError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val turn = gameState?.currentTurn ?: "-"
    val status = gameState?.status?.name ?: "DISCONNECTED"
    val mineTag = if (gameState?.currentTurn == localName) "  (YOUR TURN)" else ""

    Column(
        modifier = modifier
            .background(Color(0xCC000000))
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Text(
            text = "You: $localName",
            color = Color(0xFFB0B0B0)
        )
        Text(
            text = "Status: $status | Turn: $turn$mineTag",
            color = if (gameState?.currentTurn == localName) Color.Green else Color.White
        )
        Text(
            text = "Last tap: $lastTap",
            color = Color(0xFFB0B0B0)
        )
        Text(
            text = "Last move sent: $lastMove",
            color = Color(0xFFB0B0B0)
        )
        Text(
            text = "Units: " + units.joinToString {
                "${it.player.take(4)}:${it.type.name.take(3)}(${it.x},${it.y})"
            },
            color = Color(0xFF8090A0),
            maxLines = 3
        )
        Row(
            modifier = Modifier.padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = onClearError) {
                Text("Clear error")
            }
        }
        lastError?.let {
            Text(
                text = "ERROR: ${it.errorCode}: ${it.message}",
                color = Color.Red
            )
        }
        selected?.let {
            Text(
                text = "Selected: ${it.type} @ (${it.x}, ${it.y}) - tap a hex to move",
                color = Color.Yellow
            )
        }
    }
}
