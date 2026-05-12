package at.aau.serg.websocketbrokerdemo.grid.shape

object RectangleShape : GridShape {
    override fun isInside(col: Int, row: Int) = true
}
