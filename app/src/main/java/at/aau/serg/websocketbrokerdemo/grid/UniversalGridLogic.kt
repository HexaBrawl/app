package at.aau.serg.websocketbrokerdemo.grid

import at.aau.serg.websocketbrokerdemo.grid.input.GridInput
import at.aau.serg.websocketbrokerdemo.grid.model.GridModel

object UniversalGridLogic {

    fun handleTap(
        x: Float,
        y: Float,
        model: GridModel,
        input: GridInput,
        onCellClicked: (Int, Int) -> Unit
    ) {
        val result = input.detect(x, y, model)
        if (result != null) {
            val (col, row) = result
            onCellClicked(col, row)
        }
    }
}
