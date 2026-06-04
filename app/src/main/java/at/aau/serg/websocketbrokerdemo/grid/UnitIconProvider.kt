package at.aau.serg.websocketbrokerdemo.grid

import com.example.myapplication.R
import at.aau.serg.websocketbrokerdemo.data.serverside.PlayerColor
import at.aau.serg.websocketbrokerdemo.data.serverside.UnitType

/**
 * Provides drawable resource IDs for game units based on player color and unit type.
 */
object UnitIconProvider {

    /**
     * Returns the drawable resource ID for a given unit type and player color.
     */
    fun iconFor(color: PlayerColor, type: UnitType): Int = when (type) {
        UnitType.SKELETON -> when (color) {
            PlayerColor.RED -> R.drawable.figure_red_gravestone
            PlayerColor.BLUE -> R.drawable.figure_blue_gravestone
            PlayerColor.GREEN -> R.drawable.figure_green_gravestone
            PlayerColor.YELLOW -> R.drawable.figure_yellow_gravestone
        }
        UnitType.INFANTRY -> when (color) {
            PlayerColor.RED -> R.drawable.figure_red_infantry
            PlayerColor.BLUE -> R.drawable.figure_blue_infantry
            PlayerColor.GREEN -> R.drawable.figure_green_infantry
            PlayerColor.YELLOW -> R.drawable.figure_yellow_infantry
        }
        UnitType.CAVALRY -> when (color) {
            PlayerColor.RED -> R.drawable.figure_red_cavalry
            PlayerColor.BLUE -> R.drawable.figure_blue_cavalry
            PlayerColor.GREEN -> R.drawable.figure_green_cavalry
            PlayerColor.YELLOW -> R.drawable.figure_yellow_cavalry
        }
        UnitType.ARCHER -> when (color) {
            PlayerColor.RED -> R.drawable.figure_red_archer
            PlayerColor.BLUE -> R.drawable.figure_blue_archer
            PlayerColor.GREEN -> R.drawable.figure_green_archer
            PlayerColor.YELLOW -> R.drawable.figure_yellow_archer
        }
    }
}
