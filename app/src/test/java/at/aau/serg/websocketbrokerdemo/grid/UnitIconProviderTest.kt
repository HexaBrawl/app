package at.aau.serg.websocketbrokerdemo.grid

import com.example.myapplication.R
import at.aau.serg.websocketbrokerdemo.data.serverside.PlayerColor
import at.aau.serg.websocketbrokerdemo.data.serverside.UnitType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UnitIconProviderTest {

    @Test
    fun `skeleton icons are unique per color`() {
        val icons = PlayerColor.entries.map { UnitIconProvider.iconFor(it, UnitType.SKELETON) }
        assertEquals(4, icons.distinct().size)

        assertEquals(R.drawable.figure_red_gravestone, UnitIconProvider.iconFor(PlayerColor.RED, UnitType.SKELETON))
        assertEquals(R.drawable.figure_blue_gravestone, UnitIconProvider.iconFor(PlayerColor.BLUE, UnitType.SKELETON))
        assertEquals(R.drawable.figure_green_gravestone, UnitIconProvider.iconFor(PlayerColor.GREEN, UnitType.SKELETON))
        assertEquals(R.drawable.figure_yellow_gravestone, UnitIconProvider.iconFor(PlayerColor.YELLOW, UnitType.SKELETON))
    }

    @Test
    fun `infantry icons are unique per color`() {
        val icons = PlayerColor.entries.map { UnitIconProvider.iconFor(it, UnitType.INFANTRY) }
        assertEquals(4, icons.distinct().size)
    }

    @Test
    fun `cavalry icons are unique per color`() {
        val icons = PlayerColor.entries.map { UnitIconProvider.iconFor(it, UnitType.CAVALRY) }
        assertEquals(4, icons.distinct().size)
    }

    @Test
    fun `archer icons are unique per color`() {
        val icons = PlayerColor.entries.map { UnitIconProvider.iconFor(it, UnitType.ARCHER) }
        assertEquals(4, icons.distinct().size)
    }
}
