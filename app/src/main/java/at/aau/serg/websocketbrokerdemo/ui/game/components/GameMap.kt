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
import at.aau.serg.websocketbrokerdemo.data.serverside.UnitType
import at.aau.serg.websocketbrokerdemo.grid.input.HexInput
import at.aau.serg.websocketbrokerdemo.grid.layout.HexLayout
import at.aau.serg.websocketbrokerdemo.grid.library.GridSpec
import at.aau.serg.websocketbrokerdemo.grid.model.GridModel
import at.aau.serg.websocketbrokerdemo.grid.model.UnitData
import at.aau.serg.websocketbrokerdemo.grid.renderer.ComposeHexDrawer
import at.aau.serg.websocketbrokerdemo.grid.renderer.HexRenderer
import at.aau.serg.websocketbrokerdemo.grid.shape.RectangleShape
import at.aau.serg.websocketbrokerdemo.grid.ui.UniversalGrid
import at.aau.serg.websocketbrokerdemo.ui.game.camera.CameraState
import at.aau.serg.websocketbrokerdemo.ui.game.camera.cameraControls
import com.example.myapplication.R

/**
 * Composable fuer die Spielkarte (Hintergrund + Hex-Grid + Kamera).
 *
 * Verantwortlich nur fuer die Darstellung und Tap-Erfassung. Welche
 * Aktion ein Tap konkret ausloest, entscheidet der Aufrufer ueber
 * [onCellTapped].
 *
 * Tap-Handling Note:
 *  Der innere Canvas misst sich mit `wrapContentSize` (effektiv 0x0)
 *  und der Kamera-Layer wendet ein graphicsLayer-Transform aufs ganze
 *  Mapping an. Der innere Canvas wuerde nie Taps sehen. Wir fangen
 *  daher Taps auf der aeusseren Box und nutzen die "pre-transform"-
 *  Position, die Compose bereits zurueckgerechnet hat.
 */
@Composable
fun GameMap(
    spec: GridSpec,
    units: List<GameUnit>,
    camera: CameraState,
    onCellTapped: (tapX: Float, tapY: Float, pixelToCell: (Float, Float) -> Pair<Int, Int>?) -> Unit,
    modifier: Modifier = Modifier
) {
    val layout = remember(spec) {
        HexLayout(hexSize = 60f, rows = spec.rows, cols = spec.cols)
    }

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
            .pointerInput(spec, units) {
                detectTapGestures { offset ->
                    onCellTapped(offset.x, offset.y) { gridX, gridY ->
                        layout.pixelToCell(gridX, gridY)
                    }
                }
            }
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_map2),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        if (camera.viewportSize.value != IntSize.Zero) {
            val gridModel = GridModel(
                width = spec.cols,
                height = spec.rows,
                shape = RectangleShape,
                layout = layout,
                units = unitData
            )

            val drawer = remember { ComposeHexDrawer() }
            val renderer = remember { HexRenderer(drawer) }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                UniversalGrid(
                    model = gridModel,
                    renderer = renderer,
                    input = HexInput,
                    // Innerer Canvas hat 0x0; Taps werden von der aeusseren Box gefangen.
                    onCellClicked = { _, _ -> },
                    modifier = Modifier.wrapContentSize()
                )
            }
        }

        LaunchedEffect(spec, camera.viewportSize.value) {
            if (camera.viewportSize.value != IntSize.Zero) {
                camera.offsetX.floatValue = 0f
                camera.offsetY.floatValue = 0f

                val gridWidth = (spec.cols - 1) * (60f * 1.5f)
                val gridHeight = (spec.rows - 1) * (60f * 1.732f)

                val scaleX = camera.viewportSize.value.width / gridWidth
                val scaleY = camera.viewportSize.value.height / gridHeight

                camera.scale.floatValue = minOf(scaleX, scaleY) * 0.7f
            }
        }
    }
}
