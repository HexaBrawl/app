package at.aau.serg.websocketbrokerdemo.grid

import androidx.compose.ui.graphics.Color
import at.aau.serg.websocketbrokerdemo.data.serverside.Player
import at.aau.serg.websocketbrokerdemo.data.serverside.PlayerColor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PlayerColorMapTest {

    private val alice = Player(name = "Alice", color = PlayerColor.RED)
    private val bob = Player(name = "Bob", color = PlayerColor.BLUE)
    private val carol = Player(name = "Carol", color = PlayerColor.GREEN)
    private val dave = Player(name = "Dave", color = PlayerColor.YELLOW)

    private val allPlayers = listOf(alice, bob, carol, dave)

    @Test
    fun `colorFor returns the player's server color for known names`() {
        assertEquals(PlayerColor.RED.main, PlayerColorMap.colorFor("Alice", allPlayers))
        assertEquals(PlayerColor.BLUE.main, PlayerColorMap.colorFor("Bob", allPlayers))
        assertEquals(PlayerColor.GREEN.main, PlayerColorMap.colorFor("Carol", allPlayers))
        assertEquals(PlayerColor.YELLOW.main, PlayerColorMap.colorFor("Dave", allPlayers))
    }

    @Test
    fun `colorFor returns default for unknown player name`() {
        assertEquals(PlayerColorMap.DEFAULT_COLOR, PlayerColorMap.colorFor("Ghost", allPlayers))
    }

    @Test
    fun `colorFor returns default when players list is empty`() {
        assertEquals(PlayerColorMap.DEFAULT_COLOR, PlayerColorMap.colorFor("Alice", emptyList()))
    }

    @Test
    fun `colorFor direct mapping returns main color of enum`() {
        assertEquals(PlayerColor.RED.main, PlayerColorMap.colorFor(PlayerColor.RED))
        assertEquals(PlayerColor.BLUE.main, PlayerColorMap.colorFor(PlayerColor.BLUE))
        assertEquals(PlayerColor.GREEN.main, PlayerColorMap.colorFor(PlayerColor.GREEN))
        assertEquals(PlayerColor.YELLOW.main, PlayerColorMap.colorFor(PlayerColor.YELLOW))
    }

    @Test
    fun `default color is gray`() {
        assertEquals(Color.Gray, PlayerColorMap.DEFAULT_COLOR)
    }

    @Test
    fun `cellFillFor applies 50 percent alpha by default`() {
        assertEquals(
            PlayerColor.RED.main.copy(alpha = 0.5f),
            PlayerColorMap.cellFillFor("Alice", allPlayers)
        )
    }

    @Test
    fun `cellFillFor respects custom alpha`() {
        assertEquals(
            PlayerColor.BLUE.main.copy(alpha = 0.25f),
            PlayerColorMap.cellFillFor("Bob", allPlayers, alpha = 0.25f)
        )
    }

    @Test
    fun `cellFillFor falls back to translucent default color for unknown player`() {
        assertEquals(
            PlayerColorMap.DEFAULT_COLOR.copy(alpha = 0.5f),
            PlayerColorMap.cellFillFor("Ghost", allPlayers)
        )
    }

    @Test
    fun `colorFor is deterministic for same input`() {
        val first = PlayerColorMap.colorFor("Alice", allPlayers)
        val second = PlayerColorMap.colorFor("Alice", allPlayers)
        assertEquals(first, second)
    }

    // ---- Skelett-Fuellfarbe (subissue #172) -----------------------------

    @Test
    fun `skeletonCellFillFor differs from cellFillFor in RGB for known player`() {
        val live = PlayerColorMap.cellFillFor("Alice", allPlayers)
        val skeleton = PlayerColorMap.skeletonCellFillFor("Alice", allPlayers)
        val rgbDiffers = live.red != skeleton.red ||
                live.green != skeleton.green ||
                live.blue != skeleton.blue
        assertTrue(rgbDiffers)
    }

    @Test
    fun `skeletonCellFillFor keeps alpha equal to cellFillFor at default`() {
        val live = PlayerColorMap.cellFillFor("Alice", allPlayers)
        val skeleton = PlayerColorMap.skeletonCellFillFor("Alice", allPlayers)
        assertEquals(live.alpha, skeleton.alpha)
    }

    @Test
    fun `skeletonCellFillFor respects custom alpha`() {
        val skeleton = PlayerColorMap.skeletonCellFillFor("Bob", allPlayers, alpha = 0.25f)
        assertEquals(0.25f, skeleton.alpha, 0.01f)
    }

    @Test
    fun `skeletonCellFillFor for RED differs from skeletonCellFillFor for BLUE`() {
        val skeletonRed = PlayerColorMap.skeletonCellFillFor("Alice", allPlayers)
        val skeletonBlue = PlayerColorMap.skeletonCellFillFor("Bob", allPlayers)
        assertNotEquals(skeletonRed, skeletonBlue)
    }

    @Test
    fun `all four player colors stay distinguishable in their skeleton variants`() {
        val skeletons = listOf(
            PlayerColorMap.skeletonCellFillFor("Alice", allPlayers),
            PlayerColorMap.skeletonCellFillFor("Bob", allPlayers),
            PlayerColorMap.skeletonCellFillFor("Carol", allPlayers),
            PlayerColorMap.skeletonCellFillFor("Dave", allPlayers)
        )
        assertEquals(4, skeletons.toSet().size)
    }

    @Test
    fun `skeletonCellFillFor falls back to translucent gray for unknown player`() {
        assertEquals(
            PlayerColorMap.DEFAULT_COLOR.copy(alpha = 0.5f),
            PlayerColorMap.skeletonCellFillFor("Ghost", allPlayers)
        )
    }

    @Test
    fun `skeletonCellFillFor is deterministic for same input`() {
        val first = PlayerColorMap.skeletonCellFillFor("Alice", allPlayers)
        val second = PlayerColorMap.skeletonCellFillFor("Alice", allPlayers)
        assertEquals(first, second)
    }

    @Test
    fun `skeletonCellFillFor moves each channel measurably toward gray`() {
        // Bei GRAY_MIX_FACTOR = 0.7 muss jeder Kanal mindestens 40 % des
        // Abstands zu Color.Gray ueberbrueckt haben -- damit der Effekt
        // visuell auf Pergament + Alpha 0.5 garantiert sichtbar ist.
        val base = PlayerColor.RED.main
        val skel = PlayerColorMap.skeletonCellFillFor("Alice", allPlayers)
        val gray = Color.Gray
        assertTrue(distance(skel.red, gray.red) <= distance(base.red, gray.red) * 0.6f)
        assertTrue(distance(skel.green, gray.green) <= distance(base.green, gray.green) * 0.6f)
        assertTrue(distance(skel.blue, gray.blue) <= distance(base.blue, gray.blue) * 0.6f)
    }

    private fun distance(a: Float, b: Float): Float = kotlin.math.abs(a - b)
}