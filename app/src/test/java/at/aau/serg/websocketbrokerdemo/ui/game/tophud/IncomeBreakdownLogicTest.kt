package at.aau.serg.websocketbrokerdemo.ui.game.tophud

import at.aau.serg.websocketbrokerdemo.data.serverside.Field
import at.aau.serg.websocketbrokerdemo.data.serverside.GameUnit
import at.aau.serg.websocketbrokerdemo.data.serverside.Player
import at.aau.serg.websocketbrokerdemo.data.serverside.UnitType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Tests fuer IncomeBreakdownLogic.
 *
 * Pure JVM-Unit-Tests ohne Compose-Runtime -- der Punkt der Logic-
 * Schicht ist genau das, sie ohne UI-Setup pruefen zu koennen.
 *
 * Wichtigster Test ist `farmIncome plus fieldIncome equals player income`:
 * der Drift-Detektor zwischen Client-Konstanten und Server-Backend.
 */
class IncomeBreakdownLogicTest {

    private val alice = Player(name = "Alice", farms = 2, income = 10, upkeep = 12)
    private val bob = Player(name = "Bob", farms = 0, income = 0, upkeep = 0)

    private val aliceFields = listOf(
        Field(x = 0, y = 0, owner = "Alice"),
        Field(x = 1, y = 0, owner = "Alice"),
        Field(x = 2, y = 0, owner = "Alice"),
        Field(x = 3, y = 0, owner = "Alice")
    )

    private val aliceUnits = listOf(
        GameUnit(player = "Alice", type = UnitType.INFANTRY),
        GameUnit(player = "Alice", type = UnitType.ARCHER),
        GameUnit(player = "Alice", type = UnitType.CAVALRY)
    )

    @Test
    fun `buildBreakdown returns server values for the local player`() {
        val result = IncomeBreakdownLogic.buildBreakdown(
            players = listOf(alice, bob),
            units = aliceUnits,
            fields = aliceFields,
            localName = "Alice"
        )

        assertEquals(2, result.farms)
        assertEquals(6, result.farmIncome)
        assertEquals(4, result.fields)
        assertEquals(4, result.fieldIncome)
        assertEquals(10, result.grossIncome)
        assertEquals(3, result.units)
        assertEquals(12, result.upkeep)
        assertEquals(-2, result.netIncome)
    }

    @Test
    fun `buildBreakdown returns empty values for an empty player list`() {
        val result = IncomeBreakdownLogic.buildBreakdown(
            players = emptyList(),
            units = emptyList(),
            fields = emptyList(),
            localName = "Alice"
        )

        assertEquals(0, result.farms)
        assertEquals(TopHudLogic.GOLD_PER_FARM, result.goldPerFarm)
        assertEquals(TopHudLogic.GOLD_PER_FIELD, result.goldPerField)
        assertEquals(0, result.grossIncome)
        assertEquals(0, result.upkeep)
        assertEquals(0, result.netIncome)
    }

    @Test
    fun `buildBreakdown returns empty values when localName is not in players`() {
        val result = IncomeBreakdownLogic.buildBreakdown(
            players = listOf(alice),
            units = aliceUnits,
            fields = aliceFields,
            localName = "Bob"
        )

        assertEquals(0, result.farms)
        assertEquals(0, result.fields)
        assertEquals(0, result.units)
        assertEquals(0, result.grossIncome)
    }

    @Test
    fun `buildBreakdown returns empty values when localName is null`() {
        val result = IncomeBreakdownLogic.buildBreakdown(
            players = listOf(alice),
            units = aliceUnits,
            fields = aliceFields,
            localName = null
        )

        assertEquals(0, result.farms)
        assertEquals(0, result.grossIncome)
    }

    @Test
    fun `buildBreakdown ignores skeleton fields when counting`() {
        val mixedFields = listOf(
            Field(x = 0, y = 0, owner = "Alice"),
            Field(x = 1, y = 0, owner = "Alice"),
            Field(x = 2, y = 0, owner = "Alice", isSkeleton = true),
            Field(x = 3, y = 0, owner = "Alice", isSkeleton = true)
        )
        val aliceWithLessIncome = alice.copy(income = 8)

        val result = IncomeBreakdownLogic.buildBreakdown(
            players = listOf(aliceWithLessIncome),
            units = emptyList(),
            fields = mixedFields,
            localName = "Alice"
        )

        assertEquals(2, result.fields)
        assertEquals(2, result.fieldIncome)
    }

    @Test
    fun `buildBreakdown ignores skeleton and base units when counting troops`() {
        val mixedUnits = listOf(
            GameUnit(player = "Alice", type = UnitType.INFANTRY),
            GameUnit(player = "Alice", type = UnitType.SKELETON),
            GameUnit(player = "Alice", type = UnitType.BASE),
            GameUnit(player = "Bob", type = UnitType.INFANTRY)
        )

        val result = IncomeBreakdownLogic.buildBreakdown(
            players = listOf(alice),
            units = mixedUnits,
            fields = emptyList(),
            localName = "Alice"
        )

        assertEquals(1, result.units)
    }

    @Test
    fun `farmIncome plus fieldIncome equals player income`() {
        val result = IncomeBreakdownLogic.buildBreakdown(
            players = listOf(alice),
            units = aliceUnits,
            fields = aliceFields,
            localName = "Alice"
        )

        assertEquals(
            alice.income,
            result.farmIncome + result.fieldIncome,
            "Brutto-Aufschluesselung weicht von player.income ab -- " +
                "Backend-Konstante geaendert? GOLD_PER_FARM/GOLD_PER_FIELD nachziehen."
        )
    }

    @Test
    fun `netIncome equals income minus upkeep`() {
        val result = IncomeBreakdownLogic.buildBreakdown(
            players = listOf(alice),
            units = aliceUnits,
            fields = aliceFields,
            localName = "Alice"
        )

        assertEquals(alice.income - alice.upkeep, result.netIncome)
    }
}
