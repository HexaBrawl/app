package at.aau.serg.websocketbrokerdemo.ui.game.bottomhud

import at.aau.serg.websocketbrokerdemo.data.serverside.GameStatus
import at.aau.serg.websocketbrokerdemo.data.serverside.Player
import at.aau.serg.websocketbrokerdemo.data.serverside.PlayerColor
import at.aau.serg.websocketbrokerdemo.data.serverside.UnitType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Tests für BottomHudLogic.
 *
 * Diese Klasse prüft sämtliche reinen Logikfunktionen des Bottom-HUDs:
 * Gold‑Ermittlung, Spielerfarbe, Zugstatus, Farm‑Anzahl, dynamische Farm‑Preise
 * sowie Kaufbedingungen für Farms und Einheiten. Zusätzlich wird getestet,
 * dass SKELETON‑Einheiten nicht kaufbar sind und dass das Beenden des Zuges
 * nur im eigenen Zug erlaubt ist.
 *
 * Alle Tests sind side‑effect‑frei und validieren ausschließlich die
 * Berechnungslogik von BottomHudLogic.
 */

class BottomHudLogicTest {

    private val alice = Player(name = "Alice", color = PlayerColor.RED, gold = 100)
    private val bob = Player(name = "Bob", color = PlayerColor.BLUE, gold = 20)

    // ---- goldOf -------------------------------------------------------

    @Test
    fun `goldOf returns gold of local player`() {
        assertEquals(100, BottomHudLogic.goldOf(listOf(alice, bob), "Alice"))
    }

    @Test
    fun `goldOf returns 0 for unknown player`() {
        assertEquals(0, BottomHudLogic.goldOf(listOf(alice, bob), "Ghost"))
    }

    @Test
    fun `goldOf returns 0 for null name`() {
        assertEquals(0, BottomHudLogic.goldOf(listOf(alice, bob), null))
    }

    // ---- colorOf ------------------------------------------------------

    @Test
    fun `colorOf returns player color`() {
        assertEquals(PlayerColor.RED, BottomHudLogic.colorOf(listOf(alice, bob), "Alice"))
        assertEquals(PlayerColor.BLUE, BottomHudLogic.colorOf(listOf(alice, bob), "Bob"))
    }

    @Test
    fun `colorOf returns RED as default for unknown player`() {
        assertEquals(PlayerColor.RED, BottomHudLogic.colorOf(listOf(alice, bob), "Ghost"))
    }

    // ---- isMyTurn -----------------------------------------------------

    @Test
    fun `isMyTurn is true when in progress and current turn matches`() {
        assertTrue(
            BottomHudLogic.isMyTurn("Alice", GameStatus.IN_PROGRESS, "Alice")
        )
    }

    @Test
    fun `isMyTurn is false when game is not in progress`() {
        assertFalse(
            BottomHudLogic.isMyTurn("Alice", GameStatus.WAITING_FOR_PLAYERS, "Alice")
        )
        assertFalse(
            BottomHudLogic.isMyTurn("Alice", GameStatus.FINISHED, "Alice")
        )
    }

    @Test
    fun `isMyTurn is false when current turn is other player`() {
        assertFalse(
            BottomHudLogic.isMyTurn("Bob", GameStatus.IN_PROGRESS, "Alice")
        )
    }

    @Test
    fun `isMyTurn is false when current turn is null`() {
        assertFalse(
            BottomHudLogic.isMyTurn(null, GameStatus.IN_PROGRESS, "Alice")
        )
    }

    @Test
    fun `isMyTurn is false when local name is null`() {
        assertFalse(
            BottomHudLogic.isMyTurn("Alice", GameStatus.IN_PROGRESS, null)
        )
    }

    // ---- farmCountOf ---------------------------------------------------

    @Test
    fun `farmCountOf returns 0 when player has no farms`() {
        val players = listOf(Player(name = "A", farms = 0))
        val result = BottomHudLogic.farmCountOf(players, "A")
        assertEquals(0, result)
    }

    @Test
    fun `farmCountOf returns the farm count of the local player`() {
        val players = listOf(
            Player(name = "A", farms = 2),
            Player(name = "B", farms = 5)
        )

        val result = BottomHudLogic.farmCountOf(players, "A")
        assertEquals(2, result)
    }

    @Test
    fun `farmCountOf returns 0 when localName is null`() {
        val players = listOf(Player(name = "A", farms = 3))
        val result = BottomHudLogic.farmCountOf(players, null)
        assertEquals(0, result)
    }

    // ---- farmPrice ---------------------------------------------------

    @Test
    fun `farmPrice is 10 when player owns no farms`() {
        val players = listOf(Player(name = "A", farms = 0))
        val result = BottomHudLogic.farmPrice(players, "A")
        assertEquals(10, result)
    }

    @Test
    fun `farmPrice increases by 1 for each owned farm`() {
        val players = listOf(Player(name = "A", farms = 3))
        // 3 Farms → Preis = 10 + 3 = 13
        val result = BottomHudLogic.farmPrice(players, "A")
        assertEquals(13, result)
    }

    @Test
    fun `farmPrice ignores farms of other players`() {
        val players = listOf(
            Player(name = "A", farms = 0),
            Player(name = "B", farms = 2)
        )
        // Spieler A hat 0 Farms → Preis = 10
        val result = BottomHudLogic.farmPrice(players, "A")
        assertEquals(10, result)
    }



    // ---- canBuyFarm ---------------------------------------------------

    @Test
    fun `canBuyFarm is true when affordable and my turn`() {
        val players = listOf(Player(name = "A", farms = 0)) // Preis = 10
        assertTrue(
            BottomHudLogic.canBuyFarm(
                gold = 100,
                isMyTurn = true,
                players = players,
                localName = "A"
            )
        )
    }

    @Test
    fun `canBuyFarm is false when not enough gold`() {
        val players = listOf(Player(name = "A", farms = 0)) // Preis = 10
        assertFalse(
            BottomHudLogic.canBuyFarm(
                gold = 9,
                isMyTurn = true,
                players = players,
                localName = "A"
            )
        )
    }

    @Test
    fun `canBuyFarm is false when not my turn`() {
        val players = listOf(Player(name = "A", farms = 0))
        assertFalse(
            BottomHudLogic.canBuyFarm(
                gold = 100,
                isMyTurn = false,
                players = players,
                localName = "A"
            )
        )
    }

    @Test
    fun `canBuyFarm is true exactly at price boundary`() {
        val players = listOf(Player(name = "A", farms = 0)) // Preis = 10
        assertTrue(
            BottomHudLogic.canBuyFarm(
                gold = 10,
                isMyTurn = true,
                players = players,
                localName = "A"
            )
        )
    }

    @Test
    fun `canBuyFarm increases price with number of owned farms`() {
        val players = listOf(Player(name = "A", farms = 2))
        // 2 Farms → Preis = 10 + 2 = 12
        assertTrue(
            BottomHudLogic.canBuyFarm(
                gold = 12,
                isMyTurn = true,
                players = players,
                localName = "A"
            )
        )
    }

    @Test
    fun `canBuyFarm is false when gold is below dynamic price`() {
        val players = listOf(Player(name = "A", farms = 2))
        // Preis = 12, Gold = 11 → false
        assertFalse(
            BottomHudLogic.canBuyFarm(
                gold = 11,
                isMyTurn = true,
                players = players,
                localName = "A"
            )
        )
    }


    // ---- canBuyUnit ---------------------------------------------------

    @Test
    fun `canBuyUnit is true when affordable`() {
        assertTrue(BottomHudLogic.canBuyUnit(UnitType.INFANTRY, gold = 100, isMyTurn = true))
        assertTrue(BottomHudLogic.canBuyUnit(UnitType.ARCHER, gold = 100, isMyTurn = true))
        assertTrue(BottomHudLogic.canBuyUnit(UnitType.CAVALRY, gold = 100, isMyTurn = true))
    }

    @Test
    fun `canBuyUnit is false when not my turn`() {
        assertFalse(BottomHudLogic.canBuyUnit(UnitType.INFANTRY, gold = 100, isMyTurn = false))
    }

    @Test
    fun `canBuyUnit is false for SKELETON type (not purchasable)`() {
        assertFalse(BottomHudLogic.canBuyUnit(UnitType.SKELETON, gold = 1_000_000, isMyTurn = true))
    }

    @Test
    fun `canBuyUnit is false for BASE type (not purchasable)`() {
        assertFalse(BottomHudLogic.canBuyUnit(UnitType.BASE, gold = 1_000_000, isMyTurn = true))
    }

    @Test
    fun `priceOf returns positive values for purchasable units`() {
        assertTrue(BottomHudLogic.priceOf(UnitType.INFANTRY) > 0)
        assertTrue(BottomHudLogic.priceOf(UnitType.ARCHER) > 0)
        assertTrue(BottomHudLogic.priceOf(UnitType.CAVALRY) > 0)
    }

    @Test
    fun `priceOf SKELETON is unaffordable`() {
        assertEquals(Int.MAX_VALUE, BottomHudLogic.priceOf(UnitType.SKELETON))
    }

    @Test
    fun `priceOf BASE is unaffordable`() {
        assertEquals(Int.MAX_VALUE, BottomHudLogic.priceOf(UnitType.BASE))
    }

    // ---- canEndTurn ---------------------------------------------------

    @Test
    fun `canEndTurn is true on my turn`() {
        assertTrue(BottomHudLogic.canEndTurn(isMyTurn = true))
    }

    @Test
    fun `canEndTurn is false when not my turn`() {
        assertFalse(BottomHudLogic.canEndTurn(isMyTurn = false))
    }
}
