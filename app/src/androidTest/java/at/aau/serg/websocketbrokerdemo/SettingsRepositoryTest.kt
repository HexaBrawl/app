package at.aau.serg.websocketbrokerdemo

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import at.aau.serg.websocketbrokerdemo.data.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class SettingsRepositoryTest {

    @Test
    fun `default settings are correct`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = SettingsRepository(context)

        val settings = repository.settings.first()

        assertEquals("en", settings.language)
        assertEquals(true, settings.musicEnabled)
        assertEquals(0.6f, settings.musicVolume)
        assertEquals(true, settings.sfxEnabled)
    }

    @Test
    fun `language can be updated`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = SettingsRepository(context)

        repository.setLanguage("de")

        val settings = repository.settings.first()

        assertEquals("de", settings.language)
    }

    @Test
    fun `music enabled can be updated`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = SettingsRepository(context)

        repository.setMusicEnabled(false)

        val settings = repository.settings.first()

        assertEquals(false, settings.musicEnabled)
    }

    @Test
    fun `music volume is clamped between 0 and 1`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = SettingsRepository(context)

        repository.setMusicVolume(2.0f)

        val settings = repository.settings.first()

        assertTrue(settings.musicVolume <= 1.0f)
        assertTrue(settings.musicVolume >= 0.0f)
    }

    @Test
    fun `sfx enabled can be updated`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = SettingsRepository(context)

        repository.setSfxEnabled(false)

        val settings = repository.settings.first()

        assertEquals(false, settings.sfxEnabled)
    }

    @Test
    fun `multiple updates are persisted correctly`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = SettingsRepository(context)

        repository.setLanguage("fr")
        repository.setMusicEnabled(false)
        repository.setMusicVolume(0.3f)
        repository.setSfxEnabled(false)

        val settings = repository.settings.first()

        assertEquals("fr", settings.language)
        assertEquals(false, settings.musicEnabled)
        assertEquals(0.3f, settings.musicVolume)
        assertEquals(false, settings.sfxEnabled)
    }
}