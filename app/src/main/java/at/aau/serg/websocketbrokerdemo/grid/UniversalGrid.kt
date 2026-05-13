package at.aau.serg.websocketbrokerdemo.grid.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import at.aau.serg.websocketbrokerdemo.grid.UniversalGridLogic
import at.aau.serg.websocketbrokerdemo.grid.input.GridInput
import at.aau.serg.websocketbrokerdemo.grid.model.GridModel
import at.aau.serg.websocketbrokerdemo.grid.renderer.GridRenderer

@Composable
fun UniversalGrid(
    model: GridModel,
    renderer: GridRenderer,
    input: GridInput,
    onCellClicked: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier.pointerInput(model) {
            detectTapGestures { offset ->
                UniversalGridLogic.handleTap(
                    offset.x,
                    offset.y,
                    model,
                    input,
                    onCellClicked
                )
            }
        }
    ) {
        with(renderer) {
            render(model)
        }
    }
}

