package at.aau.serg.websocketbrokerdemo.data.serverside

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PlayerColorTest {

    @Test
    fun `PlayerColor values exist`() {
        assertNotNull(PlayerColor.valueOf("RED"))
        assertNotNull(PlayerColor.valueOf("BLUE"))
    }
}
