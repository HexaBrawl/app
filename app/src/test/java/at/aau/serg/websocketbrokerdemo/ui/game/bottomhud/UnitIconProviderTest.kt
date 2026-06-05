package at.aau.serg.websocketbrokerdemo.ui.game.bottomhud

import at.aau.serg.websocketbrokerdemo.data.serverside.PlayerColor
import at.aau.serg.websocketbrokerdemo.data.serverside.UnitType
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Tests fuer UnitIconProvider.
 *
 * Wir testen die Eindeutigkeit der Mappings -- jede Color+Type-Kombination
 * (ausser SKELETON) muss eine eigene Resource-ID liefern. Wir koennen nicht
 * gegen konkrete R.drawable-Werte testen, weil das nur ints sind, aber wir
 * koennen Eindeutigkeit + Aufrufbarkeit pruefen.
 */
class UnitIconProviderTest {

    private val purchasableTypes = listOf(UnitType.INFANTRY, UnitType.ARCHER, UnitType.CAVALRY)

    @Test
    fun `each color-type combo has a unique drawable id`() {
        val combos = PlayerColor.entries.flatMap { color ->
            purchasableTypes.map { type -> UnitIconProvider.iconFor(color, type) }
        }
        // 4 Farben * 3 Typen = 12 eindeutige IDs
        assertTrue(combos.size == 12)
        assertTrue(combos.toSet().size == 12, "Jede ID muss eindeutig sein")
    }

    @Test
    fun `same color but different type gives different id`() {
        PlayerColor.entries.forEach { color ->
            val infantry = UnitIconProvider.iconFor(color, UnitType.INFANTRY)
            val archer = UnitIconProvider.iconFor(color, UnitType.ARCHER)
            val cavalry = UnitIconProvider.iconFor(color, UnitType.CAVALRY)
            assertNotEquals(infantry, archer)
            assertNotEquals(archer, cavalry)
            assertNotEquals(infantry, cavalry)
        }
    }

    @Test
    fun `same type but different color gives different id`() {
        purchasableTypes.forEach { type ->
            val ids = PlayerColor.entries.map { color -> UnitIconProvider.iconFor(color, type) }
            assertTrue(ids.toSet().size == 4, "Alle 4 Farben sollen unterschiedliche IDs liefern")
        }
    }

    @Test
    fun `iconFor returns non-zero resource id for all valid combos`() {
        PlayerColor.entries.forEach { color ->
            purchasableTypes.forEach { type ->
                val id = UnitIconProvider.iconFor(color, type)
                assertTrue(id != 0, "ID fuer $color/$type darf nicht 0 sein")
            }
        }
    }

    @Test
    fun `iconFor handles SKELETON without crashing`() {
        // SKELETON ist nicht kaufbar, soll aber kein Crash ausloesen.
        PlayerColor.entries.forEach { color ->
            UnitIconProvider.iconFor(color, UnitType.SKELETON)
        }
    }
}
