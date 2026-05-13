package at.aau.serg.websocketbrokerdemo.grid.input

import at.aau.serg.websocketbrokerdemo.grid.model.GridModel

fun interface GridInput {  //fun interface = Kotlin Functional Interface

    // Gibt (col,row) zurück oder null, wenn kein Feld getroffen wurde
    fun detect(px: Float, py: Float, model: GridModel): Pair<Int, Int>?
}
