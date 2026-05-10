package at.aau.serg.websocketbrokerdemo.grid.shape

interface GridShape {

    // Sagt, ob eine Zelle (col,row) zur Form gehört
    fun isInside(col: Int, row: Int): Boolean

    // Gibt alle gültigen Zellen zurück
    fun allCells(width: Int, height: Int): Sequence<Pair<Int, Int>> =
        sequence {
            for (r in 0 until height)
                for (c in 0 until width)
                    if (isInside(c, r)) yield(c to r)
        }
}
