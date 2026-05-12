package at.aau.serg.websocketbrokerdemo.grid.library

object GridLibrary {

    val grid1v1 = GridSpec(
        rows = 8,
        cols = 8,
        name = "1v1 Grid 8x8"
    )

    val grid3Players = GridSpec(
        rows = 14,
        cols = 14,
        name = "3P Grid 14x14"
    )

    val grid4Players = GridSpec(
        rows = 20,
        cols = 20,
        name = "4P Grid 20x20"
    )

    fun forPlayers(playerCount: Int): GridSpec =
        when (playerCount) {
            2 -> grid1v1
            3 -> grid3Players
            4 -> grid4Players
            else -> grid1v1
        }
}
