package at.aau.serg.websocketbrokerdemo.grid

import com.example.myapplication.R
import at.aau.serg.websocketbrokerdemo.data.serverside.BuildingType
import at.aau.serg.websocketbrokerdemo.data.serverside.PlayerColor

/**
 * Provides drawable resource IDs for game buildings based on player color and building type.
 */
object BuildingIconProvider {

    /**
     * Returns the drawable resource ID for a given building type and player color.
     */
    fun iconFor(color: PlayerColor, type: BuildingType): Int = when (type) {
        BuildingType.CASTLE -> when (color) {
            PlayerColor.RED -> R.drawable.castle_red
            PlayerColor.BLUE -> R.drawable.castle_blue
            PlayerColor.GREEN -> R.drawable.castle_green
            PlayerColor.YELLOW -> R.drawable.castle_yellow
        }
        BuildingType.FARM -> 0
    }
}
