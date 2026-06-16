package at.aau.serg.websocketbrokerdemo.ui.game

import androidx.compose.ui.unit.IntSize
import at.aau.serg.websocketbrokerdemo.data.serverside.Field
import at.aau.serg.websocketbrokerdemo.data.serverside.GameUnit
import at.aau.serg.websocketbrokerdemo.data.serverside.Move
import at.aau.serg.websocketbrokerdemo.data.serverside.Player
import at.aau.serg.websocketbrokerdemo.data.serverside.UnitType
import at.aau.serg.websocketbrokerdemo.grid.HexGridLogic
import at.aau.serg.websocketbrokerdemo.grid.MapLayout

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

        /** Eine gekaufte Einheit soll an einer Zelle platziert werden. */
        data class PlaceUnit(val type: UnitType, val x: Int, val y: Int) : TapAction()

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
     * Entscheidet was ein Tap auf eine Zelle ausloesen soll.
     *
     * Wenn der Spieler im Platzierungs-Modus ist (placementMode != null),
     * loest jeder Tap eine PlaceUnit-Aktion aus -- der Server validiert
     * dann ob die Zelle dem Spieler gehoert.
     *
     * Sonst gelten die bisherigen Regeln (Select / Move / Ignore).
     */
    fun decideTapAction(
        col: Int,
        row: Int,
        units: List<GameUnit>,
        localName: String?,
        currentlySelected: GameUnit?,
        placementMode: UnitType?
    ): TapAction {
        if (placementMode != null) {
            return TapAction.PlaceUnit(placementMode, col, row)
        }

        val clickedUnit = findClickableUnit(units, col, row)

        if (clickedUnit != null && clickedUnit.player == localName) {
            // Die Hauptbasis kann sich nicht bewegen -> nicht selektierbar.
            // Wir geben Ignore zurueck (nicht durchfallen lassen), damit
            // ein Tap auf die eigene Basis nicht versehentlich einen
            // ExecuteMove zur eigenen Basis ausloest.
            if (clickedUnit.type == UnitType.BASE) {
                return TapAction.Ignore
            }
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

    /**
     * True, wenn der Zug auf eine feindliche Einheit (nicht Skelett) zielt —
     * also ein Angriff ist. Wird genutzt um den Kampf-SFX auszuloesen.
     */
    fun isAttackMove(move: Move, units: List<GameUnit>, localName: String?): Boolean {
        val target = findClickableUnit(units, move.toX, move.toY) ?: return false
        return target.player != localName
    }

    /**
     * Liefert die Namen aller Mitspieler (= NICHT der lokale Spieler),
     * die laut Server-State gerade `connected == false` sind.
     *
     * Reihenfolge stabil (Eingangsreihenfolge), damit das UI keine
     * Sortier-Sprünge produziert. Liefert eine leere Liste wenn nur der
     * lokale Spieler weg ist (dann zeigt der eigene Reconnecting-Overlay
     * — der DisconnectedPlayerOverlay ist nur fuer die ANDEREN gedacht).
     */
    fun disconnectedOtherPlayerNames(
        players: List<Player>,
        localName: String?
    ): List<String> =
        players
            .filter { !it.connected && it.name != localName }
            .map { it.name }

    /**
     * Berechnet die Felder, auf die [unit] in diesem Zug ziehen kann.
     *
     * Reichweiten-Regel (Spec, Pfad-basiert):
     *  - Entfernung 1: jede Zelle (eigen, neutral, feindlich) — der Zug
     *    darf neutrale/feindliche Felder erobern, aber nur 1 Schritt weit.
     *  - Entfernung 2: nur erreichbar, wenn ein gemeinsames Nachbarfeld
     *    von Source und Target DEM SPIELER gehoert. Der zweite Schritt
     *    darf dann auf ein beliebiges Feld (eigen / neutral / feindlich)
     *    fuehren. Bild: "uebers eigene Gebiet marschieren und am Rand
     *    erobern".
     *  - BASE bewegt sich nicht.
     *  - Bereits gezogene Einheit -> leer.
     *
     * Das eigene Stand-Feld der Einheit wird NICHT mit zurueckgegeben.
     */
    fun reachableCells(
        unit: GameUnit,
        fields: List<Field>,
        layout: MapLayout
    ): Set<Pair<Int, Int>> {
        if (unit.hasMovedThisTurn) return emptySet()
        if (unit.type == UnitType.BASE) return emptySet()
        val ownedCells: Set<Pair<Int, Int>> = fields
            .asSequence()
            .filter { it.owner == unit.player }
            .map { it.x to it.y }
            .toSet()
        val sx = unit.x
        val sy = unit.y
        val allCells = HexGridLogic.allCells(layout).toList()
        // Eigene Felder die direkt neben der Quelle liegen -- das sind die
        // moeglichen "Trittsteine" fuer den 2-Felder-Marsch.
        val ownNeighbors = allCells.filter { (c, r) ->
            (c to r) in ownedCells && HexGridLogic.hexDistance(sx, sy, c, r) == 1
        }
        return allCells
            .filter { (col, row) ->
                if (col == sx && row == sy) return@filter false
                when (HexGridLogic.hexDistance(sx, sy, col, row)) {
                    1 -> true
                    2 -> ownNeighbors.any { (ic, ir) ->
                        HexGridLogic.hexDistance(col, row, ic, ir) == 1
                    }
                    else -> false
                }
            }
            .toSet()
    }

    /**
     * Berechnet die Felder, auf denen der Spieler eine gekaufte Einheit
     * platzieren darf (Platzierungs-Modus).
     *
     * Spiegelt exakt die Server-Validierung in buyUnit wider:
     *  - Feld gehoert dem Spieler (owner == [localName])
     *  - Feld ist NICHT abgeschnitten (isSkeleton == false)
     *  - Feld ist NICHT von einer eigenen Einheit besetzt -- inklusive der
     *    eigenen BASE/Hauptburg. Eigene Skelett-Reste zaehlen NICHT als
     *    besetzt, weil der Server sie beim Platzieren entfernt.
     *
     * Liefert eine leere Menge, wenn [localName] null ist.
     */
    fun placeableCells(
        fields: List<Field>,
        units: List<GameUnit>,
        localName: String?
    ): Set<Pair<Int, Int>> {
        if (localName == null) return emptySet()
        val occupiedByOwn = units
            .asSequence()
            .filter { it.player == localName && it.type != UnitType.SKELETON }
            .map { it.x to it.y }
            .toSet()
        return fields
            .asSequence()
            .filter { it.owner == localName && !it.isSkeleton }
            .map { it.x to it.y }
            .filter { it !in occupiedByOwn }
            .toSet()
    }

    /**
     * Ergebnis der Feld-Hervorhebung auf der Karte: welche Zellen aktiv
     * aufleuchten (gueltige Ziele) und welche abgedunkelt werden (ungueltig
     * bzw. ausserhalb der Reichweite).
     */
    data class CellHighlight(
        val highlighted: Set<Pair<Int, Int>>,
        val darkened: Set<Pair<Int, Int>>
    )

    /**
     * Bestimmt die Feld-Hervorhebung fuer den aktuellen Interaktions-Modus.
     *
     *  - Platzierungs-Modus ([placementMode] != null): hervorgehoben sind die
     *    eigenen, freien Felder ([placeableCells]).
     *  - Bewegungs-Modus ([selected] != null): hervorgehoben sind die per
     *    Reichweite erreichbaren Felder ([reachableCells]) ABZUEGLICH der
     *    Felder, die von einer eigenen Einheit (Truppe ODER Hauptburg)
     *    besetzt sind -- dorthin laesst der Server keinen Zug zu, also werden
     *    sie nicht hervorgehoben, sondern abgedunkelt.
     *  - Kein Modus aktiv: nichts hervorgehoben, nichts abgedunkelt.
     *
     * Abgedunkelt wird jeweils alles ausser den hervorgehobenen Feldern und
     * (im Bewegungs-Modus) dem Standfeld der selektierten Einheit. Ist nichts
     * hervorgehoben, bleibt auch nichts abgedunkelt.
     */
    fun cellHighlighting(
        placementMode: UnitType?,
        selected: GameUnit?,
        fields: List<Field>,
        units: List<GameUnit>,
        localName: String?,
        layout: MapLayout
    ): CellHighlight {
        val highlighted: Set<Pair<Int, Int>> = when {
            placementMode != null -> placeableCells(fields, units, localName)
            selected != null ->
                reachableCells(selected, fields, layout) - ownOccupiedCells(units, selected.player)
            else -> emptySet()
        }
        if (highlighted.isEmpty()) return CellHighlight(emptySet(), emptySet())

        val allCells = HexGridLogic.allCells(layout).toSet()
        val ownStand = setOfNotNull(selected?.let { it.x to it.y })
        return CellHighlight(
            highlighted = highlighted,
            darkened = allCells - highlighted - ownStand
        )
    }

    /**
     * Zellen, die von einer eigenen, "lebenden" Einheit des Spielers [player]
     * besetzt sind -- inklusive Hauptburg (BASE), ABER ohne Skelett-Reste:
     * auf ein eigenes Skelett darf man ziehen, um es einzunehmen (der Server
     * entfernt das Skelett beim Betreten des Feldes). Solche Felder blockieren
     * die Bewegung also nicht und werden daher NICHT abgedunkelt.
     */
    private fun ownOccupiedCells(units: List<GameUnit>, player: String): Set<Pair<Int, Int>> =
        units.asSequence()
            .filter { it.player == player && it.type != UnitType.SKELETON }
            .map { it.x to it.y }
            .toSet()
}
