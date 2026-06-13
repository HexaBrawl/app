package at.aau.serg.websocketbrokerdemo.network

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * Tests fuer [ReconnectLogic.retryUntilSuccess].
 *
 * Verwendet einen fake-Delay-Hook anstelle des echten `delay()`, sodass
 * die Tests sofort durchlaufen — die Logik darf nicht von realer Wallclock-
 * Zeit abhaengen.
 */
class ReconnectLogicTest {

    private val recordedDelays = mutableListOf<Long>()
    private val fakeDelay: suspend (Long) -> Unit = { recordedDelays += it }

    @Test
    fun `returns true on first attempt success without delaying`() = runTest {
        var attempts = 0
        val result = ReconnectLogic.retryUntilSuccess(
            maxAttempts = 5,
            delayMillis = 100,
            attempt = { attempts++; true },
            delayFn = fakeDelay
        )
        assertTrue(result)
        assertEquals(1, attempts)
        assertEquals(0, recordedDelays.size)
    }

    @Test
    fun `returns true when success comes on later attempt`() = runTest {
        var attempts = 0
        val result = ReconnectLogic.retryUntilSuccess(
            maxAttempts = 5,
            delayMillis = 100,
            attempt = { attempts++; attempts == 3 },
            delayFn = fakeDelay
        )
        assertTrue(result)
        assertEquals(3, attempts)
        // 2 delays between 3 attempts
        assertEquals(listOf(100L, 100L), recordedDelays)
    }

    @Test
    fun `returns false when all attempts fail`() = runTest {
        var attempts = 0
        val result = ReconnectLogic.retryUntilSuccess(
            maxAttempts = 4,
            delayMillis = 50,
            attempt = { attempts++; false },
            delayFn = fakeDelay
        )
        assertFalse(result)
        assertEquals(4, attempts)
    }

    @Test
    fun `does not delay after the last failed attempt`() = runTest {
        ReconnectLogic.retryUntilSuccess(
            maxAttempts = 3,
            delayMillis = 250,
            attempt = { false },
            delayFn = fakeDelay
        )
        // 3 attempts -> 2 delays (between attempts), no trailing delay
        assertEquals(2, recordedDelays.size)
    }

    @Test
    fun `single attempt with failure returns false and does not delay`() = runTest {
        var attempts = 0
        val result = ReconnectLogic.retryUntilSuccess(
            maxAttempts = 1,
            delayMillis = 999,
            attempt = { attempts++; false },
            delayFn = fakeDelay
        )
        assertFalse(result)
        assertEquals(1, attempts)
        assertEquals(0, recordedDelays.size)
    }

    @Test
    fun `single attempt with success returns true`() = runTest {
        val result = ReconnectLogic.retryUntilSuccess(
            maxAttempts = 1,
            delayMillis = 999,
            attempt = { true },
            delayFn = fakeDelay
        )
        assertTrue(result)
    }

    @Test
    fun `rejects maxAttempts less than 1`() {
        assertThrows<IllegalArgumentException> {
            kotlinx.coroutines.runBlocking {
                ReconnectLogic.retryUntilSuccess(
                    maxAttempts = 0,
                    delayMillis = 100,
                    attempt = { true }
                )
            }
        }
    }
}
