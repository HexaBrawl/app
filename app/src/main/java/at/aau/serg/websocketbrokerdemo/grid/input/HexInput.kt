package at.aau.serg.websocketbrokerdemo.grid.input

import at.aau.serg.websocketbrokerdemo.grid.model.GridModel

object HexInput : GridInput {

    override fun detect(px: Float, py: Float, model: GridModel): Pair<Int, Int>? {
        // Layout entscheidet, ob ein Hex getroffen wurde
        return model.layout.pixelToCell(px, py)
    }
}
