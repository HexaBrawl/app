package at.aau.serg.websocketbrokerdemo.grid

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PlayerColorsTest {

    @BeforeEach
    fun resetState() {
        val field = PlayerColors::class.java.getDeclaredField("playerColors")
        field.isAccessible = true
        (field.get(PlayerColors) as MutableMap<*, *>).clear()
    }

    @Test
    fun samePlayerGetsSameColor() {
        val color1 = PlayerColors.getColorForPlayer("Alice")
        val color2 = PlayerColors.getColorForPlayer("Alice")

        assertEquals(color1, color2)
    }

    @Test
    fun differentPlayersGetDifferentColorsInitially() {
        val color1 = PlayerColors.getColorForPlayer("Alice")
        val color2 = PlayerColors.getColorForPlayer("Bob")

        assertNotEquals(color1, color2)
    }

    @Test
    fun colorsCycleWhenExceedingListSize() {
        val players = listOf("A", "B", "C", "D", "E", "F", "G")

        val assignedColors = players.map {
            PlayerColors.getColorForPlayer(it)
        }

        assertEquals(assignedColors[0], assignedColors[6])
    }

    @Test
    fun newPlayerDoesNotAffectExistingAssignments() {
        val aliceColor = PlayerColors.getColorForPlayer("Alice")

        PlayerColors.getColorForPlayer("Bob")
        PlayerColors.getColorForPlayer("Charlie")

        val aliceColorAgain = PlayerColors.getColorForPlayer("Alice")

        assertEquals(aliceColor, aliceColorAgain)
    }
}