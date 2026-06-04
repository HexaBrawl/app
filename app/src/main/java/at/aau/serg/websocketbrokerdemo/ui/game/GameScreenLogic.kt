package at.aau.serg.websocketbrokerdemo.ui.game

import androidx.compose.ui.unit.IntSize
import at.aau.serg.websocketbrokerdemo.data.serverside.GameUnit
import at.aau.serg.websocketbrokerdemo.data.serverside.Move
import at.aau.serg.websocketbrokerdemo.data.serverside.UnitType

/**
 * Pure Logik des GameScreens.
 *
 * Alle Funktionen sind seiteneffekt-frei und damit ohne Compose-Runtime
 * testbar. Das ViewModel ruft sie auf und reagiert auf die Ergebnisse.
 */
object GameScreenLogic {

    /**
     * Moegliche Aktionen die ein Tap auf eine Hex-Zelle ausloesen kann.
     */
    sealed class TapAction {
        /** Eine eigene Einheit wurde (erstmals oder neu) ausgewaehlt. */
        data class Select(val unit: GameUnit) : TapAction()

        /** Ein Zug soll ausgefuehrt werden. */
        data class ExecuteMove(val move: Move) : TapAction()

        /** Tap hatte keinen Effekt (z. B. leeres Feld ohne Auswahl). */
        data object Ignore : TapAction()
    }

    /**
     * Wandelt eine Tap-Position in der View in eine Grid-Zelle.
     *
     * @return (col, row) der getroffenen Zelle, oder null wenn die
     *         Viewport-Groesse noch nicht bekannt ist oder kein Hex
     *         getroffen wurde.
     */
    fun tapToCell(
        tapX: Float,
        tapY: Float,
        viewportSize: IntSize,
        pixelToCell: (Float, Float) -> Pair<Int, Int>?
    ): Pair<Int, Int>? {
        if (viewportSize == IntSize.Zero) return null
        val gridX = tapX - viewportSize.width / 2f
        val gridY = tapY - viewportSize.height / 2f
        return pixelToCell(gridX, gridY)
    }

    /**
     * Entscheidet was ein Tap auf eine bestimmte Zelle ausloesen soll.
     *
     *  - Keine Auswahl + eigene Einheit getippt        -> Select
     *  - Bestehende Auswahl + eigene Einheit getippt   -> Select (Wechsel)
     *  - Bestehende Auswahl + andere Zelle             -> ExecuteMove
     *  - Keine Auswahl + leere oder gegnerische Zelle  -> Ignore
     */
    fun decideTapAction(
        col: Int,
        row: Int,
        units: List<GameUnit>,
        localName: String?,
        currentlySelected: GameUnit?
    ): TapAction {
        val clickedUnit = findClickableUnit(units, col, row)

        if (clickedUnit != null && clickedUnit.player == localName) {
            return TapAction.Select(clickedUnit)
        }

        if (currentlySelected == null) {
            return TapAction.Ignore
        }

        return TapAction.ExecuteMove(
            Move(
                player = currentlySelected.player,
                type = currentlySelected.type,
                fromX = currentlySelected.x,
                fromY = currentlySelected.y,
                toX = col,
                toY = row
            )
        )
    }

    /**
     * Findet die "klickbare" Einheit auf einer Zelle. Skelette gelten
     * als Dekoration und werden ignoriert.
     */
    private fun findClickableUnit(units: List<GameUnit>, col: Int, row: Int): GameUnit? =
        units.firstOrNull { it.x == col && it.y == row && it.type != UnitType.SKELETON }
}
