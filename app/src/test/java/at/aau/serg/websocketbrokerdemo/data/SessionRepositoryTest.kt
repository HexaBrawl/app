package at.aau.serg.websocketbrokerdemo.data

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

/**
 * Tests fuer [SessionRepository].
 *
 * Plain-class-Tests ohne Mocking: das Repository ist ein reiner In-Memory-
 * State-Holder, also auch ohne Coroutines/Compose-Runtime testbar.
 */
class SessionRepositoryTest {

    @Test
    fun `defaults are all null`() {
        val repo = SessionRepository()
        assertNull(repo.playerName)
        assertNull(repo.joinCode)
        assertNull(repo.roomId)
    }

    @Test
    fun `set values are read back`() {
        val repo = SessionRepository()
        repo.playerName = "Alice"
        repo.joinCode = "CODE12"
        repo.roomId = "uuid-1234"

        assertEquals("Alice", repo.playerName)
        assertEquals("CODE12", repo.joinCode)
        assertEquals("uuid-1234", repo.roomId)
    }

    @Test
    fun `clear resets all three fields`() {
        val repo = SessionRepository().apply {
            playerName = "Alice"
            joinCode = "CODE12"
            roomId = "uuid-1234"
        }

        repo.clear()

        assertNull(repo.playerName)
        assertNull(repo.joinCode)
        assertNull(repo.roomId)
    }

    @Test
    fun `clear on an already empty repository is a no-op`() {
        val repo = SessionRepository()
        repo.clear()
        assertNull(repo.playerName)
        assertNull(repo.joinCode)
        assertNull(repo.roomId)
    }

    @Test
    fun `setting one field does not touch the others`() {
        val repo = SessionRepository().apply {
            playerName = "Alice"
            joinCode = "CODE12"
        }
        repo.roomId = "uuid-1234"

        assertEquals("Alice", repo.playerName)
        assertEquals("CODE12", repo.joinCode)
        assertEquals("uuid-1234", repo.roomId)
    }
}
