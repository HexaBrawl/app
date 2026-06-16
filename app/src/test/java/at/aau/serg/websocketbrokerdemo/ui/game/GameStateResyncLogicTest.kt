package at.aau.serg.websocketbrokerdemo.ui.game

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Tests fuer GameStateResyncLogic.
 *
 * Konstanten-Sanity: die Werte muessen positiv sein, sonst wuerde
 * der Resync ueberhaupt nicht laufen (0 Requests) oder unbegrenzt
 * im Block haengen (negativer Delay).
 *
 * Wert-Tests dokumentieren die aktuell gewaehlten Defaults; bei
 * bewusster Aenderung des Resync-Verhaltens muessen die Tests
 * mitgezogen werden.
 */
class GameStateResyncLogicTest {

    @Test
    fun `RESYNC_REQUESTS is positive`() {
        assertTrue(GameStateResyncLogic.RESYNC_REQUESTS > 0)
    }

    @Test
    fun `RESYNC_DELAY_MS is positive`() {
        assertTrue(GameStateResyncLogic.RESYNC_DELAY_MS > 0L)
    }

    @Test
    fun `RESYNC_REQUESTS matches expected default`() {
        assertEquals(5, GameStateResyncLogic.RESYNC_REQUESTS)
    }

    @Test
    fun `RESYNC_DELAY_MS matches expected default`() {
        assertEquals(400L, GameStateResyncLogic.RESYNC_DELAY_MS)
    }
}
