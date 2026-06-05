package at.aau.serg.websocketbrokerdemo.ui.game

import androidx.compose.ui.unit.IntSize
import at.aau.serg.websocketbrokerdemo.data.serverside.GameUnit
import at.aau.serg.websocketbrokerdemo.data.serverside.Move
import at.aau.serg.websocketbrokerdemo.data.serverside.UnitType
import org.junit.jupiter.api.Assertions.assertEquals
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
}
