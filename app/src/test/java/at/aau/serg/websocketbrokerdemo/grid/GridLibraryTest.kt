package at.aau.serg.websocketbrokerdemo.grid

import at.aau.serg.websocketbrokerdemo.grid.library.GridLibrary
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GridLibraryTest {

    @Test
    fun `forPlayers should return 1v1 grid for 2 players`() {
        val spec = GridLibrary.forPlayers(2)
        assertSame(GridLibrary.grid1v1, spec)
    }

    @Test
    fun `forPlayers should return 3 player grid for 3 players`() {
        val spec = GridLibrary.forPlayers(3)
        assertSame(GridLibrary.grid3Players, spec)
    }

    @Test
    fun `forPlayers should return 4 player grid for 4 players`() {
        val spec = GridLibrary.forPlayers(4)
        assertSame(GridLibrary.grid4Players, spec)
    }

    @Test
    fun `forPlayers should default to 1v1 grid for invalid player count`() {
        val spec = GridLibrary.forPlayers(999)
        assertSame(GridLibrary.grid1v1, spec)
    }
}
