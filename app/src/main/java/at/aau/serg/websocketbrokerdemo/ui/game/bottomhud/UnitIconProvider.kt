package at.aau.serg.websocketbrokerdemo.ui.game.bottomhud

import androidx.annotation.DrawableRes
import at.aau.serg.websocketbrokerdemo.data.serverside.PlayerColor
import at.aau.serg.websocketbrokerdemo.data.serverside.UnitType
import com.example.myapplication.R

/**
 * Mapping von [PlayerColor] x [UnitType] auf das passende Drawable.
 *
 * Erwartet folgende Naming-Konvention der Drawables im
 * res/drawable-Verzeichnis:
 *   unit_<color>_<type>   z. B. unit_red_infantry, unit_blue_cavalry
 *
 * Falls die Drawables anders heissen, einfach die Eintraege in
 * [iconFor] anpassen -- das ist die zentrale Stelle fuer die
 * Drawable-Namen.
 *
 * SKELETON wird hier nicht abgebildet, weil das nur ein Map-
 * Dekorations-Typ ist und nicht vom Spieler gekauft werden kann.
 */
object UnitIconProvider {

    /**
     * Liefert die Drawable-ID fuer die Kombination aus Spielerfarbe
     * und Einheitstyp.
     */
    @DrawableRes
    fun iconFor(color: PlayerColor, type: UnitType): Int = when (color) {
        PlayerColor.RED -> when (type) {
            UnitType.INFANTRY -> R.drawable.figure_red_infantry
            UnitType.ARCHER -> R.drawable.figure_red_archer
            UnitType.CAVALRY -> R.drawable.figure_red_cavalry
            UnitType.SKELETON -> R.drawable.figure_red_gravestone
        }
        PlayerColor.BLUE -> when (type) {
            UnitType.INFANTRY -> R.drawable.figure_blue_infantry
            UnitType.ARCHER -> R.drawable.figure_blue_archer
            UnitType.CAVALRY -> R.drawable.figure_blue_cavalry
            UnitType.SKELETON -> R.drawable.figure_blue_gravestone
        }
        PlayerColor.GREEN -> when (type) {
            UnitType.INFANTRY -> R.drawable.figure_green_infantry
            UnitType.ARCHER -> R.drawable.figure_green_archer
            UnitType.CAVALRY -> R.drawable.figure_green_cavalry
            UnitType.SKELETON -> R.drawable.figure_green_gravestone
        }
        PlayerColor.YELLOW -> when (type) {
            UnitType.INFANTRY -> R.drawable.figure_yellow_infantry
            UnitType.ARCHER -> R.drawable.figure_yellow_archer
            UnitType.CAVALRY -> R.drawable.figure_yellow_cavalry
            UnitType.SKELETON -> R.drawable.figure_yellow_gravestone
        }
    }
}
