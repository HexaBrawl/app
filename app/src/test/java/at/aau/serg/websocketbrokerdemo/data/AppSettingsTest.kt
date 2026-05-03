package at.aau.serg.websocketbrokerdemo.data

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AppSettingsTest {

    @Test
    fun `default values match spec`() {
        val s = AppSettings()
        assertEquals("en", s.language)
        assertTrue(s.musicEnabled)
        assertEquals(0.6f, s.musicVolume, 0.0001f)
        assertTrue(s.sfxEnabled)
    }

    @Test
    fun `copy preserves untouched fields`() {
        val original = AppSettings(language = "de", musicVolume = 0.3f)
        val updated = original.copy(musicEnabled = false)

        assertEquals("de", updated.language)
        assertEquals(0.3f, updated.musicVolume, 0.0001f)
        assertEquals(false, updated.musicEnabled)
        assertEquals(true, updated.sfxEnabled)
    }

    @Test
    fun `equals and hashCode work for identical values`() {
        val a = AppSettings(language = "de", musicEnabled = false, musicVolume = 0.5f, sfxEnabled = true)
        val b = AppSettings(language = "de", musicEnabled = false, musicVolume = 0.5f, sfxEnabled = true)
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `differing values are not equal`() {
        val a = AppSettings(language = "de")
        val b = AppSettings(language = "en")
        assertNotEquals(a, b)
    }
}
