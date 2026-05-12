package at.aau.serg.websocketbrokerdemo.grid.model

import at.aau.serg.websocketbrokerdemo.grid.layout.GridLayout
import at.aau.serg.websocketbrokerdemo.grid.shape.GridShape

data class GridModel(
    val width: Int,
    val height: Int,
    val shape: GridShape,
    val layout: GridLayout,
    val units: List<UnitData>
)
