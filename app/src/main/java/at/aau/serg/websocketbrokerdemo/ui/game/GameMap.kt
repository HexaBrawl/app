package at.aau.serg.websocketbrokerdemo.ui.game.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import at.aau.serg.websocketbrokerdemo.data.serverside.GameUnit
import at.aau.serg.websocketbrokerdemo.data.serverside.Player
import at.aau.serg.websocketbrokerdemo.data.serverside.UnitType
import at.aau.serg.websocketbrokerdemo.grid.HexGrid
import at.aau.serg.websocketbrokerdemo.grid.HexGridLogic
import at.aau.serg.websocketbrokerdemo.grid.MapLayout
import at.aau.serg.websocketbrokerdemo.grid.UnitData
import at.aau.serg.websocketbrokerdemo.ui.game.camera.CameraState
import at.aau.serg.websocketbrokerdemo.ui.game.camera.cameraControls
import com.example.myapplication.R

/**
 * Composable fuer die Spielkarte (Hintergrund + Hex-Grid + Kamera).
 *
 * Background-Image liegt INNERHALB des cameraControls-Layers, damit
 * sich Hintergrund und Hex-Grid beim Zoomen gemeinsam bewegen --
 * sonst wirkt das Grid wie "losgeloest" vom Boden.
 *
 * Damit kein weisser Rand am Anfang auftaucht, ist die Initial-Scale
 * unten auf [CameraState.minScale] geclamped: lieber eine etwas zu
 * grosse Karte (Spieler kann panen) als ein weisser Rand drumherum.
 */
@Composable
fun GameMap(
    layout: MapLayout,
    units: List<GameUnit>,
    players: List<Player>,
    camera: CameraState,
    onCellTapped: (tapX: Float, tapY: Float, pixelToCell: (Float, Float) -> Pair<Int, Int>?) -> Unit,
    modifier: Modifier = Modifier
) {
    val unitData = remember(units) {
        units
            .filter { it.type != UnitType.SKELETON }
            .map { UnitData(it.x, it.y, it.player) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { camera.viewportSize.value = it }
            .cameraControls(camera)
            .pointerInput(layout, units) {
                detectTapGestures { offset ->
                    onCellTapped(offset.x, offset.y) { gridX, gridY ->
                        HexGridLogic.pixelToCell(gridX, gridY, layout)
                    }
                }
            }
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_map2),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        if (camera.viewportSize.value != IntSize.Zero) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                HexGrid(
                    layout = layout,
                    units = unitData,
                    players = players,
                    modifier = Modifier.wrapContentSize()
                )
            }
        }

        LaunchedEffect(layout, camera.viewportSize.value) {
            if (camera.viewportSize.value != IntSize.Zero) {
                camera.offsetX.floatValue = 0f
                camera.offsetY.floatValue = 0f

                val gridWidth = (layout.cols - 1) * (layout.hexSize * 1.5f)
                val gridHeight = (layout.rows - 1) * (layout.hexSize * 1.732f)

                val scaleX = camera.viewportSize.value.width / gridWidth
                val scaleY = camera.viewportSize.value.height / gridHeight

                // Berechnete Optimal-Scale, aber niemals unter minScale,
                // sonst entsteht ein weisser Rand um das Background.
                val computed = minOf(scaleX, scaleY) * 0.7f
                camera.scale.floatValue = computed.coerceAtLeast(camera.minScale)
            }
        }
    }
}