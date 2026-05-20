package at.aau.serg.websocketbrokerdemo.data.serverside

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GameStatusTest {

    @Test
    fun `GameStatus values exist`() {
        assertNotNull(GameStatus.valueOf("WAITING_FOR_PLAYERS"))
        assertNotNull(GameStatus.valueOf("IN_PROGRESS"))
        assertNotNull(GameStatus.valueOf("FINISHED"))
    }
}
