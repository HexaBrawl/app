package at.aau.serg.websocketbrokerdemo.grid

import com.example.myapplication.R
import at.aau.serg.websocketbrokerdemo.data.serverside.BuildingType
import at.aau.serg.websocketbrokerdemo.data.serverside.PlayerColor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BuildingIconProviderTest {

    @Test
    fun `castle icons are unique per color`() {
        val icons = PlayerColor.entries.map { BuildingIconProvider.iconFor(it, BuildingType.CASTLE) }
        assertEquals(4, icons.distinct().size)

        assertEquals(R.drawable.castle_red, BuildingIconProvider.iconFor(PlayerColor.RED, BuildingType.CASTLE))
        assertEquals(R.drawable.castle_blue, BuildingIconProvider.iconFor(PlayerColor.BLUE, BuildingType.CASTLE))
        assertEquals(R.drawable.castle_green, BuildingIconProvider.iconFor(PlayerColor.GREEN, BuildingType.CASTLE))
        assertEquals(R.drawable.castle_yellow, BuildingIconProvider.iconFor(PlayerColor.YELLOW, BuildingType.CASTLE))
    }
}
