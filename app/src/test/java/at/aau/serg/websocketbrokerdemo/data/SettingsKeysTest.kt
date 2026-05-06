package at.aau.serg.websocketbrokerdemo.data

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertTrue

/**
 * Schützt die Persistenz-Schlüssel vor versehentlichen Umbenennungen.
 *
 * Wenn jemand z. B. "language" zu "lang" ändert, würden alle bestehenden
 * Installationen ihre Sprache verlieren. Dieser Test schlägt sofort an,
 * sodass das nur bewusst (mit Migration) passieren kann.
 */
class SettingsKeysTest {

    @Test
    fun `language key name is stable`() {
        assertEquals("language", SettingsKeys.LANGUAGE.name)
    }

    @Test
    fun `music enabled key name is stable`() {
        assertEquals("music_enabled", SettingsKeys.MUSIC_ENABLED.name)
    }

    @Test
    fun `music volume key name is stable`() {
        assertEquals("music_volume", SettingsKeys.MUSIC_VOLUME.name)
    }

    @Test
    fun `sfx enabled key name is stable`() {
        assertEquals("sfx_enabled", SettingsKeys.SFX_ENABLED.name)
    }

    @Test
    fun `all keys have distinct names`() {
        val names = setOf(
            SettingsKeys.LANGUAGE.name,
            SettingsKeys.MUSIC_ENABLED.name,
            SettingsKeys.MUSIC_VOLUME.name,
            SettingsKeys.SFX_ENABLED.name
        )
        assertEquals(4, names.size, "All preference keys must be unique")
    }

    @Test
    fun `all keys start with expected prefixes`() {
        assertTrue(SettingsKeys.LANGUAGE.name.startsWith("language"))
        assertTrue(SettingsKeys.MUSIC_ENABLED.name.startsWith("music"))
        assertTrue(SettingsKeys.MUSIC_VOLUME.name.startsWith("music"))
        assertTrue(SettingsKeys.SFX_ENABLED.name.startsWith("sfx"))
    }

    @Test
    fun `keys do not accidentally contain whitespace`() {
        val keys = listOf(
            SettingsKeys.LANGUAGE.name,
            SettingsKeys.MUSIC_ENABLED.name,
            SettingsKeys.MUSIC_VOLUME.name,
            SettingsKeys.SFX_ENABLED.name
        )
        keys.forEach { key ->
            assertEquals(key.trim(), key, "Key must not contain whitespace")
        }
    }
}
