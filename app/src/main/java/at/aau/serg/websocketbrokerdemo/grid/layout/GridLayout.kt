package at.aau.serg.websocketbrokerdemo.grid.layout

interface GridLayout {

    // Größe einer Zelle (z. B. Hex-Radius)
    val cellSize: Float

    // Berechnet den Mittelpunkt einer Zelle in Pixeln
    fun cellCenter(col: Int, row: Int): Pair<Float, Float>

    // Wandelt Pixel-Koordinaten in (col,row) um
    fun pixelToCell(px: Float, py: Float): Pair<Int, Int>?
}
