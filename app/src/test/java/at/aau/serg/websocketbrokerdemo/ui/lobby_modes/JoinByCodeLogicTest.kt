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
        assertEquals("123456", JoinByCodeLogic.normalize("123456"))
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
    fun `normalize caps at 6 characters`() {
        // Auch wenn der User mehr tippt, der Code wird abgeschnitten.
        val tooLong = "ABCDEFGHIJKLMNOP"
        assertEquals(6, JoinByCodeLogic.normalize(tooLong).length)
        assertEquals("ABCDEF", JoinByCodeLogic.normalize(tooLong))
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
        // 5 Zeichen sind zu wenig (Min ist 6).
        assertFalse(JoinByCodeLogic.isValid("ABCDE"))
    }

    @Test
    fun `isValid returns true at minimum length`() {
        assertTrue(JoinByCodeLogic.isValid("123456"))
    }

    @Test
    fun `isValid returns true at maximum length`() {
        assertTrue(JoinByCodeLogic.isValid("ABCDEF"))
    }

    @Test
    fun `isValid returns true for middle length`() {
        // Da Min=Max=6, gibt es kein "middle" mehr in dem Sinne, 
        // aber wir testen einen gemischten Code.
        assertTrue(JoinByCodeLogic.isValid("AB1234"))
    }

    @Test
    fun `isValid returns false for too long code`() {
        // Auch wenn jemand das Limit umgeht (z. B. via Reflection), wird
        // ein 7-Zeichen-Code als ungueltig zurueckgewiesen.
        assertFalse(JoinByCodeLogic.isValid("ABCDEFG"))
    }
}
