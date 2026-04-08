package at.aau.serg.websocketbrokerdemo.grid

object ShapeUtils {
    //Rechteck = alle Hexagons werden gezeichnet
    fun isInShape(shape: GridShape, col: Int, row: Int): Boolean {
        return when (shape) {
            GridShape.RECTANGLE -> true
        }
    }
}