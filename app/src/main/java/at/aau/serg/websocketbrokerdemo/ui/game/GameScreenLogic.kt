package at.aau.serg.websocketbrokerdemo.ui.game

import at.aau.serg.websocketbrokerdemo.data.serverside.GameUnit
import at.aau.serg.websocketbrokerdemo.data.serverside.Move
import at.aau.serg.websocketbrokerdemo.data.serverside.UnitType
import androidx.compose.ui.unit.IntSize

/**
 * Pure Logik des GameScreens.
 *
 * Alle Funktionen sind seiteneffekt-frei und damit ohne Compose-Runtime
 * testbar. Das ViewModel ruft sie auf und reagiert auf die Ergebnisse.
 */
object GameScreenLogic {

    /**
     * Mögliche Aktionen die ein Tap auf eine Hex-Zelle ausloesen kann.
     *
     * Wird von [decideTapAction] zurueckgegeben; das ViewModel bzw. der
     * Aufrufer entscheidet dann was konkret damit passiert (State
     * setzen, Move senden, ...).
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
     * Der Camera-Layer hat den Tap bereits in das "pre-transform"-
     * Koordinatensystem zurueckgerechnet -- wir muessen nur noch den
     * Ursprung von der oberen linken Ecke in die Mitte verschieben,
     * weil der Canvas dort sein (0,0) hat.
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
     *  - Keine Auswahl + eigene Einheit getippt   -> Select
     *  - Bestehende Auswahl + eigene Einheit getippt -> Select (Wechsel)
     *  - Bestehende Auswahl + andere Zelle        -> ExecuteMove
     *  - Keine Auswahl + leere oder gegnerische Zelle -> Ignore
     */
    fun decideTapAction(
        col: Int,
        row: Int,
        units: List<GameUnit>,
        localName: String?,
        currentlySelected: GameUnit?
    ): TapAction {
        val clickedUnit = findClickableUnit(units, col, row)

        // Eigene Einheit getippt -> auswaehlen (egal ob vorher etwas
        // ausgewaehlt war)
        if (clickedUnit != null && clickedUnit.player == localName) {
            return TapAction.Select(clickedUnit)
        }

        // Ohne Auswahl und ohne eigene Einheit unter dem Finger -> nichts tun
        if (currentlySelected == null) {
            return TapAction.Ignore
        }

        // Auswahl vorhanden + Tap irgendwohin -> Move dorthin
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
     * Beschreibung des letzten Taps fuer das Debug-Panel.
     */
    fun describeTap(col: Int, row: Int, units: List<GameUnit>, localName: String?): String {
        val clickedUnit = findClickableUnit(units, col, row)
        val suffix = when {
            clickedUnit == null -> "empty"
            clickedUnit.player == localName -> "own ${clickedUnit.type}"
            else -> "enemy ${clickedUnit.type}"
        }
        return "($col,$row) $suffix"
    }

    /**
     * Beschreibung des zuletzt ausgefuehrten Zugs fuer das Debug-Panel.
     */
    fun describeMove(move: Move): String =
        "${move.type} (${move.fromX},${move.fromY}) -> (${move.toX},${move.toY})"

    /**
     * Findet die "klickbare" Einheit auf einer Zelle. Skelette gelten
     * als Dekoration und werden ignoriert -- sie koennen weder selektiert
     * noch als Ziel benutzt werden.
     */
    private fun findClickableUnit(units: List<GameUnit>, col: Int, row: Int): GameUnit? =
        units.firstOrNull { it.x == col && it.y == row && it.type != UnitType.SKELETON }
}
