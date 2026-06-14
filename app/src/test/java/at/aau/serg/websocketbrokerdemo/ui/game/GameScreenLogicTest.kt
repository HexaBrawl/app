package at.aau.serg.websocketbrokerdemo.ui.game

import androidx.compose.ui.unit.IntSize
import at.aau.serg.websocketbrokerdemo.data.serverside.Field
import at.aau.serg.websocketbrokerdemo.data.serverside.GameUnit
import at.aau.serg.websocketbrokerdemo.data.serverside.Move
import at.aau.serg.websocketbrokerdemo.data.serverside.Player
import at.aau.serg.websocketbrokerdemo.data.serverside.UnitType
import at.aau.serg.websocketbrokerdemo.grid.HexGridLogic
import at.aau.serg.websocketbrokerdemo.grid.MapLayout
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Tests fuer GameScreenLogic.
 *
 * Reine Pure-Function-Tests; das ViewModel wird separat getestet.
 */
class GameScreenLogicTest {

    private val alice = "Alice"
    private val bob = "Bob"

    private fun ownInf(x: Int, y: Int) =
        GameUnit(player = alice, x = x, y = y, type = UnitType.INFANTRY)
    private fun enemyArc(x: Int, y: Int) =
        GameUnit(player = bob, x = x, y = y, type = UnitType.ARCHER)
    private fun skeleton(x: Int, y: Int) =
        GameUnit(player = "Doom", x = x, y = y, type = UnitType.SKELETON)

    // ---- tapToCell ----------------------------------------------------

    @Test
    fun `tapToCell returns null for zero viewport`() {
        val result = GameScreenLogic.tapToCell(100f, 100f, IntSize.Zero) { _, _ -> 5 to 5 }
        assertNull(result)
    }

    @Test
    fun `tapToCell shifts origin to viewport center`() {
        var observedX = 0f
        var observedY = 0f
        GameScreenLogic.tapToCell(
            tapX = 150f, tapY = 130f,
            viewportSize = IntSize(200, 200)
        ) { x, y ->
            observedX = x
            observedY = y
            7 to 9
        }
        assertEquals(50f, observedX)
        assertEquals(30f, observedY)
    }

    @Test
    fun `tapToCell forwards pixelToCell result`() {
        val result = GameScreenLogic.tapToCell(
            tapX = 100f, tapY = 100f,
            viewportSize = IntSize(200, 200)
        ) { _, _ -> 3 to 4 }
        assertEquals(3 to 4, result)
    }

    @Test
    fun `tapToCell forwards null when no hex hit`() {
        val result = GameScreenLogic.tapToCell(
            tapX = 100f, tapY = 100f,
            viewportSize = IntSize(200, 200)
        ) { _, _ -> null }
        assertNull(result)
    }

    // ---- decideTapAction: Platzierungs-Modus -------------------------

    @Test
    fun `tap in placement mode produces PlaceUnit regardless of cell content`() {
        val action = GameScreenLogic.decideTapAction(
            col = 3, row = 4,
            units = emptyList(),
            localName = alice,
            currentlySelected = null,
            placementMode = UnitType.INFANTRY
        )
        assertTrue(action is GameScreenLogic.TapAction.PlaceUnit)
        val place = action as GameScreenLogic.TapAction.PlaceUnit
        assertEquals(UnitType.INFANTRY, place.type)
        assertEquals(3, place.x)
        assertEquals(4, place.y)
    }

    @Test
    fun `placement mode takes precedence over existing selection`() {
        // Auch wenn jemand selektiert ist, hat Placement Vorrang
        val selected = ownInf(0, 0)
        val action = GameScreenLogic.decideTapAction(
            col = 5, row = 5,
            units = listOf(selected),
            localName = alice,
            currentlySelected = selected,
            placementMode = UnitType.CAVALRY
        )
        assertTrue(action is GameScreenLogic.TapAction.PlaceUnit)
    }

    @Test
    fun `placement mode forwards the chosen UnitType`() {
        listOf(UnitType.INFANTRY, UnitType.ARCHER, UnitType.CAVALRY).forEach { type ->
            val action = GameScreenLogic.decideTapAction(
                col = 1, row = 1,
                units = emptyList(),
                localName = alice,
                currentlySelected = null,
                placementMode = type
            )
            assertTrue(action is GameScreenLogic.TapAction.PlaceUnit)
            assertEquals(type, (action as GameScreenLogic.TapAction.PlaceUnit).type)
        }
    }

    // ---- decideTapAction: normaler Flow -------------------------------

    @Test
    fun `tapping own unit without selection selects it`() {
        val unit = ownInf(3, 4)
        val action = GameScreenLogic.decideTapAction(
            col = 3, row = 4,
            units = listOf(unit),
            localName = alice,
            currentlySelected = null,
            placementMode = null
        )
        assertTrue(action is GameScreenLogic.TapAction.Select)
        assertEquals(unit, (action as GameScreenLogic.TapAction.Select).unit)
    }

    @Test
    fun `tapping another own unit while one is selected switches selection`() {
        val first = ownInf(1, 1)
        val second = ownInf(2, 2)
        val action = GameScreenLogic.decideTapAction(
            col = 2, row = 2,
            units = listOf(first, second),
            localName = alice,
            currentlySelected = first,
            placementMode = null
        )
        assertTrue(action is GameScreenLogic.TapAction.Select)
        assertEquals(second, (action as GameScreenLogic.TapAction.Select).unit)
    }

    @Test
    fun `tapping empty cell with selection produces ExecuteMove`() {
        val selected = ownInf(2, 2)
        val action = GameScreenLogic.decideTapAction(
            col = 5, row = 5,
            units = listOf(selected),
            localName = alice,
            currentlySelected = selected,
            placementMode = null
        )
        assertTrue(action is GameScreenLogic.TapAction.ExecuteMove)
        val move = (action as GameScreenLogic.TapAction.ExecuteMove).move
        assertEquals(Move(alice, UnitType.INFANTRY, 2, 2, 5, 5), move)
    }

    @Test
    fun `tapping enemy cell with selection produces ExecuteMove`() {
        val selected = ownInf(2, 2)
        val enemy = enemyArc(3, 3)
        val action = GameScreenLogic.decideTapAction(
            col = 3, row = 3,
            units = listOf(selected, enemy),
            localName = alice,
            currentlySelected = selected,
            placementMode = null
        )
        assertTrue(action is GameScreenLogic.TapAction.ExecuteMove)
    }

    @Test
    fun `tapping empty cell without selection is Ignore`() {
        val action = GameScreenLogic.decideTapAction(
            col = 5, row = 5,
            units = emptyList(),
            localName = alice,
            currentlySelected = null,
            placementMode = null
        )
        assertEquals(GameScreenLogic.TapAction.Ignore, action)
    }

    @Test
    fun `tapping enemy without selection is Ignore`() {
        val enemy = enemyArc(3, 3)
        val action = GameScreenLogic.decideTapAction(
            col = 3, row = 3,
            units = listOf(enemy),
            localName = alice,
            currentlySelected = null,
            placementMode = null
        )
        assertEquals(GameScreenLogic.TapAction.Ignore, action)
    }

    @Test
    fun `skeletons are ignored when looking for clickable unit`() {
        val skel = skeleton(3, 3)
        val action = GameScreenLogic.decideTapAction(
            col = 3, row = 3,
            units = listOf(skel),
            localName = alice,
            currentlySelected = null,
            placementMode = null
        )
        assertEquals(GameScreenLogic.TapAction.Ignore, action)
    }

    @Test
    fun `tapping own BASE returns Ignore, not Select`() {
        val base = GameUnit(player = alice, x = 4, y = 4, type = UnitType.BASE)
        val action = GameScreenLogic.decideTapAction(
            col = 4, row = 4,
            units = listOf(base),
            localName = alice,
            currentlySelected = null,
            placementMode = null
        )
        assertEquals(GameScreenLogic.TapAction.Ignore, action)
    }

    @Test
    fun `tapping own BASE with another unit selected stays Ignore`() {
        val base = GameUnit(player = alice, x = 4, y = 4, type = UnitType.BASE)
        val selected = ownInf(2, 2)
        val action = GameScreenLogic.decideTapAction(
            col = 4, row = 4,
            units = listOf(base, selected),
            localName = alice,
            currentlySelected = selected,
            placementMode = null
        )
        assertEquals(GameScreenLogic.TapAction.Ignore, action)
    }

    // ---- reachableCells ----------------------------------------------

    private val testLayout = MapLayout(rows = 7, cols = 7, hexSize = 10f, name = "test")

    private fun field(x: Int, y: Int, owner: String?) = Field(x = x, y = y, owner = owner)

    @Test
    fun `reachableCells leer wenn Einheit bereits gezogen`() {
        val unit = ownInf(3, 3).copy(hasMovedThisTurn = true)
        val fields = listOf(field(3, 3, alice))
        val result = GameScreenLogic.reachableCells(unit, fields, testLayout)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `reachableCells fuer BASE leer (Basis bewegt sich nicht)`() {
        val baseUnit = GameUnit(player = alice, x = 3, y = 3, type = UnitType.BASE)
        val fields = listOf(field(3, 3, alice))
        val result = GameScreenLogic.reachableCells(baseUnit, fields, testLayout)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `reachableCells liefert IMMER die 6 direkten Nachbarn`() {
        val unit = ownInf(3, 3)
        // Nichts ist eigen -> dist-2 ausgeschlossen, aber dist-1 muss bleiben
        val result = GameScreenLogic.reachableCells(unit, emptyList(), testLayout)
        assertEquals(6, result.size)
    }

    @Test
    fun `reachableCells erlaubt dist-1 unabhaengig vom Owner`() {
        val unit = ownInf(3, 3)
        val neighborEnemy = 3 to 2 // dist 1 von (3,3)
        val fields = listOf(field(neighborEnemy.first, neighborEnemy.second, bob))
        val result = GameScreenLogic.reachableCells(unit, fields, testLayout)
        assertTrue(result.contains(neighborEnemy))
    }

    @Test
    fun `reachableCells erlaubt dist-2 wenn ein Zwischenfeld eigen ist`() {
        val unit = ownInf(3, 3)
        // (3,3)->(2,3)->(1,3): (2,3) ist gemeinsamer Nachbar von Source und Target.
        val stepStone = 2 to 3
        val target = 1 to 3
        val fields = listOf(field(stepStone.first, stepStone.second, alice))
        val result = GameScreenLogic.reachableCells(unit, fields, testLayout)
        assertTrue(result.contains(target))
    }

    @Test
    fun `reachableCells erlaubt dist-2 auf NEUTRALES Ziel ueber eigenes Zwischenfeld`() {
        val unit = ownInf(3, 3)
        val stepStone = 2 to 3
        val neutralTarget = 1 to 3
        val fields = listOf(
            field(stepStone.first, stepStone.second, alice),
            field(neutralTarget.first, neutralTarget.second, null)
        )
        val result = GameScreenLogic.reachableCells(unit, fields, testLayout)
        assertTrue(result.contains(neutralTarget))
    }

    @Test
    fun `reachableCells erlaubt dist-2 auf FEINDLICHES Ziel ueber eigenes Zwischenfeld`() {
        val unit = ownInf(3, 3)
        val stepStone = 2 to 3
        val enemyTarget = 1 to 3
        val fields = listOf(
            field(stepStone.first, stepStone.second, alice),
            field(enemyTarget.first, enemyTarget.second, bob)
        )
        val result = GameScreenLogic.reachableCells(unit, fields, testLayout)
        assertTrue(result.contains(enemyTarget))
    }

    @Test
    fun `reachableCells verbietet dist-2 wenn KEIN Zwischenfeld eigen ist`() {
        val unit = ownInf(3, 3)
        // Niemand ist eigen -> keine 2-Feld-Bewegung
        val target = 1 to 3
        val result = GameScreenLogic.reachableCells(unit, emptyList(), testLayout)
        assertFalse(result.contains(target))
    }

    @Test
    fun `reachableCells verbietet dist-2 auf EIGENES Ziel wenn Pfad nicht eigen ist`() {
        val unit = ownInf(3, 3)
        // Target ist eigen, aber die Zwischenfelder (3,2) [nur einer hier]
        // sind nicht eigen -> nicht erreichbar.
        val target = 3 to 1
        val fields = listOf(field(target.first, target.second, alice))
        val result = GameScreenLogic.reachableCells(unit, fields, testLayout)
        assertFalse(result.contains(target))
    }

    @Test
    fun `reachableCells voll umringt von eigenem Gebiet ergibt 18 Felder`() {
        val unit = ownInf(3, 3)
        // alle Felder dem Spieler zuordnen -> 6 Nachbarn + 12 dist-2 = 18
        val fields = HexGridLogic.allCells(testLayout).map { (c, r) -> field(c, r, alice) }.toList()
        val result = GameScreenLogic.reachableCells(unit, fields, testLayout)
        assertEquals(18, result.size)
        assertFalse(result.contains(3 to 3))
    }

    @Test
    fun `reachableCells clamped auf Map-Bounds`() {
        val unit = ownInf(0, 0)
        val result = GameScreenLogic.reachableCells(unit, emptyList(), testLayout)
        assertTrue(result.isNotEmpty())
        result.forEach { (col, row) ->
            assertTrue(col in 0 until testLayout.cols)
            assertTrue(row in 0 until testLayout.rows)
        }
    }

    // ---- disconnectedOtherPlayerNames --------------------------------

    private fun player(name: String, connected: Boolean) =
        Player(name = name, connected = connected)

    @Test
    fun `disconnectedOtherPlayerNames leer wenn alle connected`() {
        val players = listOf(player(alice, true), player(bob, true))
        val result = GameScreenLogic.disconnectedOtherPlayerNames(players, alice)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `disconnectedOtherPlayerNames listet andere disconnectete Spieler`() {
        val players = listOf(player(alice, true), player(bob, false))
        val result = GameScreenLogic.disconnectedOtherPlayerNames(players, alice)
        assertEquals(listOf(bob), result)
    }

    @Test
    fun `disconnectedOtherPlayerNames ignoriert den lokalen Spieler selbst`() {
        val players = listOf(player(alice, false), player(bob, true))
        val result = GameScreenLogic.disconnectedOtherPlayerNames(players, alice)
        // Alice ist der lokale Spieler -> der eigene Reconnecting-Overlay
        // uebernimmt; diese Liste soll nur die ANDEREN abdecken.
        assertTrue(result.isEmpty())
    }

    @Test
    fun `disconnectedOtherPlayerNames listet mehrere Disconnects`() {
        val players = listOf(
            player(alice, true),
            player(bob, false),
            player("Carol", false)
        )
        val result = GameScreenLogic.disconnectedOtherPlayerNames(players, alice)
        assertEquals(listOf(bob, "Carol"), result)
    }

    @Test
    fun `disconnectedOtherPlayerNames mit null localName behandelt alle als andere`() {
        val players = listOf(player(alice, false), player(bob, false))
        val result = GameScreenLogic.disconnectedOtherPlayerNames(players, null)
        assertEquals(listOf(alice, bob), result)
    }

    // ---- placeableCells ----------------------------------------------

    @Test
    fun `placeableCells liefert nur eigene freie Felder`() {
        val fields = listOf(
            field(1, 1, alice),
            field(2, 2, alice),
            field(3, 3, null),
            field(4, 4, bob)
        )
        val result = GameScreenLogic.placeableCells(fields, emptyList(), alice)
        assertEquals(setOf(1 to 1, 2 to 2), result)
    }

    @Test
    fun `placeableCells schliesst von eigener Truppe besetzte Felder aus`() {
        val fields = listOf(field(1, 1, alice), field(2, 2, alice))
        val units = listOf(ownInf(2, 2))
        val result = GameScreenLogic.placeableCells(fields, units, alice)
        assertEquals(setOf(1 to 1), result)
    }

    @Test
    fun `placeableCells schliesst die eigene BASE aus`() {
        val fields = listOf(field(2, 2, alice))
        val units = listOf(GameUnit(player = alice, x = 2, y = 2, type = UnitType.BASE))
        val result = GameScreenLogic.placeableCells(fields, units, alice)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `placeableCells schliesst Skelett-Felder aus`() {
        val fields = listOf(
            field(1, 1, alice),
            Field(x = 2, y = 2, owner = alice, isSkeleton = true)
        )
        val result = GameScreenLogic.placeableCells(fields, emptyList(), alice)
        assertEquals(setOf(1 to 1), result)
    }

    @Test
    fun `placeableCells wertet eigenes Skelett-Feld als frei`() {
        // Eigene Skelett-Reste werden beim Platzieren entfernt -> Feld bleibt frei.
        val fields = listOf(field(1, 1, alice))
        val units = listOf(GameUnit(player = alice, x = 1, y = 1, type = UnitType.SKELETON))
        val result = GameScreenLogic.placeableCells(fields, units, alice)
        assertEquals(setOf(1 to 1), result)
    }

    @Test
    fun `placeableCells leer wenn localName null`() {
        val fields = listOf(field(1, 1, alice))
        val result = GameScreenLogic.placeableCells(fields, emptyList(), null)
        assertTrue(result.isEmpty())
    }

    // ---- cellHighlighting --------------------------------------------

    @Test
    fun `cellHighlighting im Placement-Modus hebt eigene freie Felder hervor`() {
        val fields = listOf(field(1, 1, alice), field(2, 2, alice))
        val units = listOf(ownInf(2, 2)) // (2,2) besetzt -> nicht platzierbar
        val result = GameScreenLogic.cellHighlighting(
            placementMode = UnitType.INFANTRY,
            selected = null,
            fields = fields,
            units = units,
            localName = alice,
            layout = testLayout
        )
        assertEquals(setOf(1 to 1), result.highlighted)
        assertTrue((1 to 1) !in result.darkened)
        assertTrue((2 to 2) in result.darkened) // eigenes, aber besetztes Feld abgedunkelt
    }

    @Test
    fun `cellHighlighting im Bewegungs-Modus dunkelt von eigener Truppe blockierte Felder ab`() {
        val mover = ownInf(3, 3)
        val blocked = 3 to 2 // dist-1 Nachbar, von eigener Truppe besetzt
        val units = listOf(mover, ownInf(blocked.first, blocked.second))
        val result = GameScreenLogic.cellHighlighting(
            placementMode = null,
            selected = mover,
            fields = emptyList(),
            units = units,
            localName = alice,
            layout = testLayout
        )
        assertTrue(blocked !in result.highlighted) // blockiert -> nicht hervorgehoben
        assertTrue(blocked in result.darkened)      // blockiert -> abgedunkelt
    }

    @Test
    fun `cellHighlighting im Bewegungs-Modus blockiert die eigene Hauptburg`() {
        val mover = ownInf(3, 3)
        val baseCell = 3 to 2
        val units = listOf(
            mover,
            GameUnit(player = alice, x = baseCell.first, y = baseCell.second, type = UnitType.BASE)
        )
        val result = GameScreenLogic.cellHighlighting(
            null, mover, emptyList(), units, alice, testLayout
        )
        assertTrue(baseCell !in result.highlighted)
        assertTrue(baseCell in result.darkened)
    }

    @Test
    fun `cellHighlighting im Bewegungs-Modus haelt feindlich besetzte Felder als Ziel`() {
        val mover = ownInf(3, 3)
        val enemyCell = 3 to 2 // dist-1 Nachbar, Gegner drauf -> Angriff erlaubt
        val units = listOf(mover, enemyArc(enemyCell.first, enemyCell.second))
        val result = GameScreenLogic.cellHighlighting(
            null, mover, emptyList(), units, alice, testLayout
        )
        assertTrue(enemyCell in result.highlighted)
    }

    @Test
    fun `cellHighlighting dunkelt das Standfeld der selektierten Einheit nicht ab`() {
        val mover = ownInf(3, 3)
        val result = GameScreenLogic.cellHighlighting(
            null, mover, emptyList(), listOf(mover), alice, testLayout
        )
        assertTrue((3 to 3) !in result.darkened)
        assertTrue((3 to 3) !in result.highlighted)
    }

    @Test
    fun `cellHighlighting ohne Modus hebt nichts hervor und dunkelt nichts ab`() {
        val result = GameScreenLogic.cellHighlighting(
            null, null, emptyList(), emptyList(), alice, testLayout
        )
        assertTrue(result.highlighted.isEmpty())
        assertTrue(result.darkened.isEmpty())
    }
}
