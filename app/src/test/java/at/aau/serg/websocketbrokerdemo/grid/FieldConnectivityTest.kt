package at.aau.serg.websocketbrokerdemo.grid

import at.aau.serg.websocketbrokerdemo.data.serverside.Field
import at.aau.serg.websocketbrokerdemo.data.serverside.GameUnit
import at.aau.serg.websocketbrokerdemo.data.serverside.UnitType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class FieldConnectivityTest {

    private fun baseUnit(player: String, x: Int, y: Int) =
        GameUnit(player = player, x = x, y = y, type = UnitType.BASE)

    @Test
    fun `connected chain includes all linked owner cells`() {
        // Kette (0,0) - (1,0) - (2,0) - (3,0), alle Alice
        val fields = listOf(
            Field(0, 0, owner = "Alice"),
            Field(1, 0, owner = "Alice"),
            Field(2, 0, owner = "Alice"),
            Field(3, 0, owner = "Alice")
        )
        val units = listOf(baseUnit("Alice", 0, 0))

        val connected = FieldConnectivity.connectedFields(fields, units, "Alice")

        assertEquals(setOf(0 to 0, 1 to 0, 2 to 0, 3 to 0), connected)
    }

    @Test
    fun `isolated cell behind enemy break is not connected`() {
        // (0,0) Alice (Basis), (1,0) Bob (Bruecke), (2,0) Alice (isoliert)
        val fields = listOf(
            Field(0, 0, owner = "Alice"),
            Field(1, 0, owner = "Bob"),
            Field(2, 0, owner = "Alice")
        )
        val units = listOf(baseUnit("Alice", 0, 0))

        val connected = FieldConnectivity.connectedFields(fields, units, "Alice")

        assertTrue((0 to 0) in connected)
        assertFalse((2 to 0) in connected)
    }

    @Test
    fun `gap in own chain breaks connectivity`() {
        // (0,0) Alice, (1,0) FEHLT (kein Field), (2,0) Alice
        val fields = listOf(
            Field(0, 0, owner = "Alice"),
            Field(2, 0, owner = "Alice")
        )
        val units = listOf(baseUnit("Alice", 0, 0))

        val connected = FieldConnectivity.connectedFields(fields, units, "Alice")

        assertEquals(setOf(0 to 0), connected)
    }

    @Test
    fun `isSkeleton flag does not block topology`() {
        // Auch wenn das mittlere Feld als skeleton markiert ist, gilt es
        // topologisch als verbunden -- die Frontend-BFS ignoriert den Flag.
        val fields = listOf(
            Field(0, 0, owner = "Alice"),
            Field(1, 0, owner = "Alice", isSkeleton = true),
            Field(2, 0, owner = "Alice")
        )
        val units = listOf(baseUnit("Alice", 0, 0))

        val connected = FieldConnectivity.connectedFields(fields, units, "Alice")

        assertEquals(setOf(0 to 0, 1 to 0, 2 to 0), connected)
    }

    @Test
    fun `player without base unit returns empty set`() {
        val fields = listOf(Field(0, 0, owner = "Alice"))
        val connected = FieldConnectivity.connectedFields(fields, emptyList(), "Alice")
        assertTrue(connected.isEmpty())
    }

    @Test
    fun `base unit on field of other owner returns empty set`() {
        val fields = listOf(Field(0, 0, owner = "Bob"))
        val units = listOf(baseUnit("Alice", 0, 0))
        val connected = FieldConnectivity.connectedFields(fields, units, "Alice")
        assertTrue(connected.isEmpty())
    }

    @Test
    fun `hexNeighbors returns six positions for even and odd columns`() {
        assertEquals(6, FieldConnectivity.hexNeighbors(2, 2).size)
        assertEquals(6, FieldConnectivity.hexNeighbors(3, 2).size)
    }

    @Test
    fun `hexNeighbors is consistent with reverse lookup`() {
        // Wenn B Nachbar von A ist, muss A auch Nachbar von B sein.
        val a = 3 to 4
        for ((nx, ny) in FieldConnectivity.hexNeighbors(a.first, a.second)) {
            assertTrue(
                a in FieldConnectivity.hexNeighbors(nx, ny),
                "Reverse lookup fehlgeschlagen fuer ($nx,$ny)"
            )
        }
    }

    // ---- isOwnedCellDead ---------------------------------------------

    @Test
    fun `isOwnedCellDead false fuer verbundenes lebendes Feld`() {
        val field = Field(1, 0, owner = "Alice")
        val dead = FieldConnectivity.isOwnedCellDead(
            field = field,
            owner = "Alice",
            unitOnCell = null,
            connectedCells = setOf(0 to 0, 1 to 0)
        )
        assertFalse(dead)
    }

    @Test
    fun `isOwnedCellDead true wenn Field als skeleton markiert`() {
        val field = Field(1, 0, owner = "Alice", isSkeleton = true)
        val dead = FieldConnectivity.isOwnedCellDead(
            field = field,
            owner = "Alice",
            unitOnCell = null,
            connectedCells = setOf(1 to 0) // sogar verbunden -> Flag gewinnt
        )
        assertTrue(dead)
    }

    @Test
    fun `isOwnedCellDead true wenn eigene Skelett-Einheit auf dem Feld steht`() {
        val field = Field(1, 0, owner = "Alice")
        val skeletonUnit = GameUnit(player = "Alice", x = 1, y = 0, type = UnitType.SKELETON)
        val dead = FieldConnectivity.isOwnedCellDead(
            field = field,
            owner = "Alice",
            unitOnCell = skeletonUnit,
            connectedCells = setOf(1 to 0)
        )
        assertTrue(dead)
    }

    @Test
    fun `isOwnedCellDead true wenn Feld nicht mit Basis verbunden`() {
        val field = Field(2, 0, owner = "Alice")
        val dead = FieldConnectivity.isOwnedCellDead(
            field = field,
            owner = "Alice",
            unitOnCell = null,
            connectedCells = setOf(0 to 0) // (2,0) nicht enthalten
        )
        assertTrue(dead)
    }

    @Test
    fun `isOwnedCellDead ignoriert fremde Skelett-Einheit als Owner-Marker`() {
        // Skelett gehoert Bob, Feld gehoert Alice und ist verbunden -> lebend.
        val field = Field(1, 0, owner = "Alice")
        val enemySkeleton = GameUnit(player = "Bob", x = 1, y = 0, type = UnitType.SKELETON)
        val dead = FieldConnectivity.isOwnedCellDead(
            field = field,
            owner = "Alice",
            unitOnCell = enemySkeleton,
            connectedCells = setOf(1 to 0)
        )
        assertFalse(dead)
    }

    @Test
    fun `disjoint outer cluster is correctly excluded`() {
        // Szenario aus dem Screenshot vom User:
        // Basis oben, eine eigene Kette nach unten, die durch ein
        // gegnerisches Feld unterbrochen wird. Ein Cluster aus 2 Feldern
        // dahinter (die X-Felder) ist nicht verbunden.
        val fields = listOf(
            Field(2, 1, owner = "Red"),   // Basis
            Field(2, 2, owner = "Red"),
            Field(2, 3, owner = "Blue"),  // Bruecke vom Gegner
            Field(2, 4, owner = "Red"),   // X1
            Field(2, 5, owner = "Red")    // X2
        )
        val units = listOf(baseUnit("Red", 2, 1))

        val connected = FieldConnectivity.connectedFields(fields, units, "Red")

        assertTrue((2 to 1) in connected)
        assertTrue((2 to 2) in connected)
        assertFalse((2 to 4) in connected)
        assertFalse((2 to 5) in connected)
    }
}