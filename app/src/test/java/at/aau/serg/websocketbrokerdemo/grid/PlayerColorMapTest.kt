package at.aau.serg.websocketbrokerdemo.grid

import androidx.compose.ui.graphics.Color
import at.aau.serg.websocketbrokerdemo.data.serverside.Player
import at.aau.serg.websocketbrokerdemo.data.serverside.PlayerColor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Tests fuer das umgebaute PlayerColorMap.
 *
 * Frueher: Hash-basiert. Jetzt: Lookup ueber GameState.players. Die
 * Logik ist seiteneffekt-frei und gut testbar.
 */
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
        // Mehrfacher Aufruf gibt das gleiche Ergebnis (keine versteckte
        // State-Mutation).
        val first = PlayerColorMap.colorFor("Alice", allPlayers)
        val second = PlayerColorMap.colorFor("Alice", allPlayers)
        assertEquals(first, second)
    }
}
