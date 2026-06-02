package at.aau.serg.websocketbrokerdemo.ui.lobby_modes

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Tests fuer JoinByCodeLogic.
 *
 * Decken die Eingabe-Normalisierung und Gueltigkeits-Pruefung ab, die
 * der JoinByCodeDialog beim Tippen anwendet. Da JoinByCodeLogic keine
 * Compose-Abhaengigkeiten hat, sind die Tests ohne Coroutinen-/Mocking-
 * Setup minimal.
 */
class JoinByCodeLogicTest {

    // ---- normalize ------------------------------------------------------

    @Test
    fun `normalize uppercases letters`() {
        assertEquals("ABCD", JoinByCodeLogic.normalize("abcd"))
    }

    @Test
    fun `normalize keeps digits`() {
        assertEquals("12345678", JoinByCodeLogic.normalize("12345678"))
    }

    @Test
    fun `normalize mixes letters and digits`() {
        assertEquals("AB12CD", JoinByCodeLogic.normalize("ab12cd"))
    }

    @Test
    fun `normalize strips spaces`() {
        assertEquals("ABCD", JoinByCodeLogic.normalize("a b c d"))
    }

    @Test
    fun `normalize strips special characters`() {
        // Punkte, Bindestriche, Emojis -- alles raus.
        assertEquals("ABC", JoinByCodeLogic.normalize("a.b-c!"))
        assertEquals("X", JoinByCodeLogic.normalize("x\uD83D\uDE00"))
    }

    @Test
    fun `normalize caps at 8 characters`() {
        // Auch wenn der User mehr tippt, der Code wird abgeschnitten.
        val tooLong = "ABCDEFGHIJKLMNOP"
        assertEquals(8, JoinByCodeLogic.normalize(tooLong).length)
        assertEquals("ABCDEFGH", JoinByCodeLogic.normalize(tooLong))
    }

    @Test
    fun `normalize returns empty string for empty input`() {
        assertEquals("", JoinByCodeLogic.normalize(""))
    }

    @Test
    fun `normalize returns empty string when input has only invalid chars`() {
        // Wenn nichts Erlaubtes uebrig bleibt, kommt ein leerer String zurueck.
        assertEquals("", JoinByCodeLogic.normalize("...---"))
    }

    // ---- isValid --------------------------------------------------------

    @Test
    fun `isValid returns false for empty code`() {
        assertFalse(JoinByCodeLogic.isValid(""))
    }

    @Test
    fun `isValid returns false for too short code`() {
        // 3 Zeichen sind zu wenig (Min ist 4).
        assertFalse(JoinByCodeLogic.isValid("ABC"))
    }

    @Test
    fun `isValid returns true at minimum length`() {
        assertTrue(JoinByCodeLogic.isValid("ABCD"))
    }

    @Test
    fun `isValid returns true at maximum length`() {
        assertTrue(JoinByCodeLogic.isValid("ABCDEFGH"))
    }

    @Test
    fun `isValid returns true for middle length`() {
        assertTrue(JoinByCodeLogic.isValid("ABC123"))
    }

    @Test
    fun `isValid returns false for too long code`() {
        // Auch wenn jemand das Limit umgeht (z. B. via Reflection), wird
        // ein 9-Zeichen-Code als ungueltig zurueckgewiesen.
        assertFalse(JoinByCodeLogic.isValid("ABCDEFGHI"))
    }
}
