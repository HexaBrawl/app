package at.aau.serg.websocketbrokerdemo.grid

import com.example.myapplication.R
import at.aau.serg.websocketbrokerdemo.data.serverside.PlayerColor
import at.aau.serg.websocketbrokerdemo.data.serverside.UnitType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class UnitIconProviderTest {

    @Test
    fun `skeleton icon is the same for all colors`() {
        val redSkeleton = UnitIconProvider.iconFor(PlayerColor.RED, UnitType.SKELETON)
        val blueSkeleton = UnitIconProvider.iconFor(PlayerColor.BLUE, UnitType.SKELETON)
        val greenSkeleton = UnitIconProvider.iconFor(PlayerColor.GREEN, UnitType.SKELETON)
        val yellowSkeleton = UnitIconProvider.iconFor(PlayerColor.YELLOW, UnitType.SKELETON)

        assertEquals(R.drawable.figure_skeleton, redSkeleton)
        assertEquals(redSkeleton, blueSkeleton)
        assertEquals(redSkeleton, greenSkeleton)
        assertEquals(redSkeleton, yellowSkeleton)
    }

    @Test
    fun `infantry icons are unique per color`() {
        val icons = PlayerColor.entries.map { UnitIconProvider.iconFor(it, UnitType.INFANTRY) }
        assertEquals(4, icons.distinct().size, "Each color should have a unique infantry icon")
        
        assertEquals(R.drawable.figure_red_infantry, UnitIconProvider.iconFor(PlayerColor.RED, UnitType.INFANTRY))
        assertEquals(R.drawable.figure_blue_infantry, UnitIconProvider.iconFor(PlayerColor.BLUE, UnitType.INFANTRY))
        assertEquals(R.drawable.figure_green_infantry, UnitIconProvider.iconFor(PlayerColor.GREEN, UnitType.INFANTRY))
        assertEquals(R.drawable.figure_yellow_infantry, UnitIconProvider.iconFor(PlayerColor.YELLOW, UnitType.INFANTRY))
    }

    @Test
    fun `cavalry icons are unique per color`() {
        val icons = PlayerColor.entries.map { UnitIconProvider.iconFor(it, UnitType.CAVALRY) }
        assertEquals(4, icons.distinct().size, "Each color should have a unique cavalry icon")

        assertEquals(R.drawable.figure_red_cavalry, UnitIconProvider.iconFor(PlayerColor.RED, UnitType.CAVALRY))
        assertEquals(R.drawable.figure_blue_cavalry, UnitIconProvider.iconFor(PlayerColor.BLUE, UnitType.CAVALRY))
        assertEquals(R.drawable.figure_green_cavalry, UnitIconProvider.iconFor(PlayerColor.GREEN, UnitType.CAVALRY))
        assertEquals(R.drawable.figure_yellow_cavalry, UnitIconProvider.iconFor(PlayerColor.YELLOW, UnitType.CAVALRY))
    }

    @Test
    fun `archer icons are unique per color`() {
        val icons = PlayerColor.entries.map { UnitIconProvider.iconFor(it, UnitType.ARCHER) }
        assertEquals(4, icons.distinct().size, "Each color should have a unique archer icon")

        assertEquals(R.drawable.figure_red_archer, UnitIconProvider.iconFor(PlayerColor.RED, UnitType.ARCHER))
        assertEquals(R.drawable.figure_blue_archer, UnitIconProvider.iconFor(PlayerColor.BLUE, UnitType.ARCHER))
        assertEquals(R.drawable.figure_green_archer, UnitIconProvider.iconFor(PlayerColor.GREEN, UnitType.ARCHER))
        assertEquals(R.drawable.figure_yellow_archer, UnitIconProvider.iconFor(PlayerColor.YELLOW, UnitType.ARCHER))
    }

    @Test
    fun `castle icons are unique per color`() {
        val icons = PlayerColor.entries.map { UnitIconProvider.castleFor(it) }
        assertEquals(4, icons.distinct().size, "Each color should have a unique castle icon")

        assertEquals(R.drawable.castle_red, UnitIconProvider.castleFor(PlayerColor.RED))
        assertEquals(R.drawable.castle_blue, UnitIconProvider.castleFor(PlayerColor.BLUE))
        assertEquals(R.drawable.castle_green, UnitIconProvider.castleFor(PlayerColor.GREEN))
        assertEquals(R.drawable.castle_yellow, UnitIconProvider.castleFor(PlayerColor.YELLOW))
    }

    @Test
    fun `icons for different types are different for same color`() {
        val color = PlayerColor.RED
        val infantry = UnitIconProvider.iconFor(color, UnitType.INFANTRY)
        val cavalry = UnitIconProvider.iconFor(color, UnitType.CAVALRY)
        val archer = UnitIconProvider.iconFor(color, UnitType.ARCHER)
        val skeleton = UnitIconProvider.iconFor(color, UnitType.SKELETON)

        assertNotEquals(infantry, cavalry)
        assertNotEquals(infantry, archer)
        assertNotEquals(infantry, skeleton)
        assertNotEquals(cavalry, archer)
        assertNotEquals(cavalry, skeleton)
        assertNotEquals(archer, skeleton)
    }
}
