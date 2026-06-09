package at.aau.serg.websocketbrokerdemo.data.serverside

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class UnitTypeTest {

    @Test
    fun `beats logic is correct`() {
        assertTrue(UnitType.INFANTRY.beats(UnitType.CAVALRY))
        assertTrue(UnitType.CAVALRY.beats(UnitType.ARCHER))
        assertTrue(UnitType.ARCHER.beats(UnitType.INFANTRY))
    }

    @Test
    fun `beats logic returns false for non-winning matchups`() {
        assertFalse(UnitType.INFANTRY.beats(UnitType.ARCHER))
        assertFalse(UnitType.CAVALRY.beats(UnitType.INFANTRY))
        assertFalse(UnitType.ARCHER.beats(UnitType.CAVALRY))
        assertFalse(UnitType.SKELETON.beats(UnitType.INFANTRY))
        assertFalse(UnitType.INFANTRY.beats(UnitType.INFANTRY))
        // BASE ist keine Kampf-Einheit und kann niemanden schlagen
        assertFalse(UnitType.BASE.beats(UnitType.INFANTRY))
        assertFalse(UnitType.BASE.beats(UnitType.ARCHER))
        assertFalse(UnitType.BASE.beats(UnitType.CAVALRY))
    }

    @Test
    fun `UnitType values exist`() {
        assertNotNull(UnitType.valueOf("ARCHER"))
        assertNotNull(UnitType.valueOf("INFANTRY"))
        assertNotNull(UnitType.valueOf("CAVALRY"))
        assertNotNull(UnitType.valueOf("SKELETON"))
        assertNotNull(UnitType.valueOf("BASE"))
    }

    @Test
    fun `BASE is not part of the rock-paper-scissors cycle`() {
        // BASE darf nicht in der BEATS-Map auftauchen - sie ist ein
        // stationaeres Ziel, kein kaempfender Einheitentyp.
        assertFalse(UnitType.BEATS.containsKey(UnitType.BASE))
        assertFalse(UnitType.BEATS.containsValue(UnitType.BASE))
    }
}
