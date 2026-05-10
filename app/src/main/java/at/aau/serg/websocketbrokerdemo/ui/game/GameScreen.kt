package at.aau.serg.websocketbrokerdemo.ui.game

import MyStomp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import at.aau.serg.websocketbrokerdemo.grid.ui.UniversalGrid
import at.aau.serg.websocketbrokerdemo.grid.model.GridModel
import at.aau.serg.websocketbrokerdemo.grid.model.UnitData
import at.aau.serg.websocketbrokerdemo.grid.shape.RectangleShape
import at.aau.serg.websocketbrokerdemo.grid.layout.HexLayout
import at.aau.serg.websocketbrokerdemo.grid.renderer.HexRenderer
import at.aau.serg.websocketbrokerdemo.grid.input.HexInput
import at.aau.serg.websocketbrokerdemo.grid.library.GridLibrary
import at.aau.serg.websocketbrokerdemo.ui.game.camera.CameraState
import at.aau.serg.websocketbrokerdemo.ui.game.camera.cameraControls
import com.example.myapplication.R

@Composable
fun GameScreen(
    myStomp: MyStomp,
    responseState: State<String>,
    playerCount: Int = 2
) {

    val mapSizeFactor = 1.0f

    // Neue Camera-Engine
    val camera = remember { CameraState(mapSizeFactor = mapSizeFactor) }

    // Grid anhand der Spieleranzahl auswählen
    val spec = GridLibrary.forPlayers(playerCount)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { camera.viewportSize.value = it }
            .cameraControls(camera)
    ) {

        // 🖼️ Hintergrund
        Image(
            painter = painterResource(id = R.drawable.bg_map2),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // 🧩 GRID
        if (camera.viewportSize.value != IntSize.Zero) {

            val gridModel = GridModel(
                width = spec.cols,
                height = spec.rows,
                shape = RectangleShape,
                layout = HexLayout(
                    hexSize = 60f,   // feste Hexagon-Größe
                    rows = spec.rows,
                    cols = spec.cols
                ),
                units = listOf(
                    UnitData(3, 4, "Player 1"),
                    UnitData(5, 2, "Player 2")
                )
            )

            // ⭐ ZENTRIERTE BOX FÜR DAS GRID
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                UniversalGrid(
                    model = gridModel,
                    renderer = HexRenderer,
                    input = HexInput,
                    onCellClicked = { col, row ->
                        println("Hex clicked: $col, $row")
                    },
                    modifier = Modifier.wrapContentSize()
                )
            }
        }

        // ⭐ AUTO-CENTER + AUTO-ZOOM
        LaunchedEffect(spec, camera.viewportSize.value) {
            if (camera.viewportSize.value != IntSize.Zero) {

                // Kamera auf die Grid-Mitte setzen
                camera.offsetX.floatValue = 0f
                camera.offsetY.floatValue = 0f

                // Auto-zoom, damit das Grid sichtbar ist
                val gridWidth = (spec.cols - 1) * (60f * 1.5f)
                val gridHeight = (spec.rows - 1) * (60f * 1.732f)

                val scaleX = camera.viewportSize.value.width / gridWidth
                val scaleY = camera.viewportSize.value.height / gridHeight

                camera.scale.floatValue = minOf(scaleX, scaleY) * 0.7f
            }
        }
    }

    // 📱 UI (nicht zoombar)
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.weight(1f))

        Column(modifier = Modifier.padding(16.dp)) {
            Text("Server Response: ${responseState.value}")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { myStomp.connect() }) { Text("Connect") }
                Button(onClick = { myStomp.sendHello() }) { Text("Send Hello") }
                Button(onClick = { myStomp.sendJson() }) { Text("Send JSON") }
            }
        }
    }
}
