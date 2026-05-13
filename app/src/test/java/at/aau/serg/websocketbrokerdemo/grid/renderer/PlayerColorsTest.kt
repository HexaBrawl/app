package at.aau.serg.websocketbrokerdemo.grid.renderer

import android.graphics.Color
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PlayerColorsTest {

    @BeforeEach
    fun reset() {
        // Reset internal map via reflection (sauberste Lösung ohne Codeänderung)
        val field = PlayerColors::class.java.getDeclaredField("playerColors")
        field.isAccessible = true
        (field.get(null) as MutableMap<*, *>).clear()
    }

    @Test
    fun `first player should get first color`() {
        val color = PlayerColors.getColorForPlayer("A")
        Assertions.assertEquals(Color.RED, color)
    }

    @Test
    fun `second player should get second color`() {
        PlayerColors.getColorForPlayer("A")
        val color = PlayerColors.getColorForPlayer("B")
        Assertions.assertEquals(Color.BLUE, color)
    }

    @Test
    fun `same player should always get same color`() {
        val c1 = PlayerColors.getColorForPlayer("A")
        val c2 = PlayerColors.getColorForPlayer("A")
        Assertions.assertEquals(c1, c2)
    }

    @Test
    fun `colors should rotate when more players than colors exist`() {
        PlayerColors.getColorForPlayer("A") // RED
        PlayerColors.getColorForPlayer("B") // BLUE
        PlayerColors.getColorForPlayer("C") // GREEN
        PlayerColors.getColorForPlayer("D") // YELLOW

        val colorE = PlayerColors.getColorForPlayer("E") // back to RED
        Assertions.assertEquals(Color.RED, colorE)
    }
}