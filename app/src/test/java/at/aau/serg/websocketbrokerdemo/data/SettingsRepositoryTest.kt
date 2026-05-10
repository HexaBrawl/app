package at.aau.serg.websocketbrokerdemo.data

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesOf
import at.aau.serg.websocketbrokerdemo.ui.settings.SettingsViewModel
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsRepositoryTest {

    private lateinit var context: Context
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var repository: SettingsRepository

    @BeforeEach
    fun setUp() {
        context = mockk(relaxed = true)
        dataStore = mockk()

        // WICHTIG: echtes leeres Preferences-Objekt
        every { dataStore.data } returns flowOf(emptyPreferences())
    }

    @Test
    fun `settings maps preferences to AppSettings`() = runTest {
        mockkObject(LocaleCache)
        every { LocaleCache.get(context) } returns "xx"

        val prefs = preferencesOf(
            SettingsKeys.LANGUAGE to "de",
            SettingsKeys.MUSIC_ENABLED to false,
            SettingsKeys.MUSIC_VOLUME to 0.3f,
            SettingsKeys.SFX_ENABLED to false
        )

        // WICHTIG: VOR Repository-Erstellung stubben
        every { dataStore.data } returns flowOf(prefs)

        repository = SettingsRepository(dataStore, context)

        val result = repository.settings.first()

        assertEquals("de", result.language)
        assertEquals(false, result.musicEnabled)
        assertEquals(0.3f, result.musicVolume)
        assertEquals(false, result.sfxEnabled)
    }



    @Test
    fun `setLanguage updates DataStore and LocaleCache`() = runTest {
        repository = SettingsRepository(dataStore, context)
        coEvery { dataStore.updateData(any()) } returns mockk()

        mockkObject(LocaleCache)
        every { LocaleCache.set(context, "de") } just Runs

        repository.setLanguage("de")

        coVerify { dataStore.updateData(any()) }
        verify { LocaleCache.set(context, "de") }
    }

    @Test
    fun `setMusicEnabled updates DataStore`() = runTest {
        repository = SettingsRepository(dataStore, context)
        coEvery { dataStore.updateData(any()) } returns mockk()

        repository.setMusicEnabled(false)

        coVerify { dataStore.updateData(any()) }
    }

    @Test
    fun `setMusicVolume updates DataStore`() = runTest {
        repository = SettingsRepository(dataStore, context)
        coEvery { dataStore.updateData(any()) } returns mockk()

        repository.setMusicVolume(2.5f)

        coVerify { dataStore.updateData(any()) }
    }

    @Test
    fun `setSfxEnabled updates DataStore`() = runTest {
        repository = SettingsRepository(dataStore, context)
        coEvery { dataStore.updateData(any()) } returns mockk()

        repository.setSfxEnabled(true)

        coVerify { dataStore.updateData(any()) }
    }

    @Test
    fun `settings uses fallback language when not set`() = runTest {
        mockkObject(LocaleCache)
        every { LocaleCache.get(context) } returns "fallback"

        val prefs = preferencesOf() // leer

        every { dataStore.data } returns flowOf(prefs)
        repository = SettingsRepository(dataStore, context)

        val result = repository.settings.first()

        assertEquals("fallback", result.language)
    }

    @Test
    fun `settings uses default musicEnabled when not set`() = runTest {
        val prefs = preferencesOf() // leer

        every { dataStore.data } returns flowOf(prefs)
        repository = SettingsRepository(dataStore, context)

        val result = repository.settings.first()

        assertEquals(true, result.musicEnabled)
    }


    @Test
    fun `settings uses default musicVolume when not set`() = runTest {
        val prefs = preferencesOf()

        every { dataStore.data } returns flowOf(prefs)
        repository = SettingsRepository(dataStore, context)

        val result = repository.settings.first()

        assertEquals(0.6f, result.musicVolume)
    }


    @Test
    fun `settings uses default sfxEnabled when not set`() = runTest {
        val prefs = preferencesOf()

        every { dataStore.data } returns flowOf(prefs)
        repository = SettingsRepository(dataStore, context)

        val result = repository.settings.first()

        assertEquals(true, result.sfxEnabled)
    }

    @Test
    fun `settings uses default musicEnabled when missing`() = runTest {
        val prefs = preferencesOf()

        every { dataStore.data } returns flowOf(prefs)

        repository = SettingsRepository(dataStore, context)

        val result = repository.settings.first()

        assertEquals(true, result.musicEnabled)
    }

    @Test
    fun `settingsDataStore delegate compiles`() {
        val ctx = mockk<Context>(relaxed = true)
        // Zugriff löst den Delegaten aus, aber DataStore wird NICHT wirklich erzeugt
        ctx.settingsDataStore
    }

    @Test
    fun `settings emits updated values when preferences change`() = runTest {
        val prefs1 = preferencesOf(SettingsKeys.LANGUAGE to "de")
        val prefs2 = preferencesOf(SettingsKeys.LANGUAGE to "fr")

        every { dataStore.data } returns flowOf(prefs1, prefs2)
        repository = SettingsRepository(dataStore, context)

        val results = repository.settings.toList()

        assertEquals("de", results[0].language)
        assertEquals("fr", results[1].language)
    }

    @Test
    fun `settings uses empty string when LocaleCache returns empty`() = runTest {
        mockkObject(LocaleCache)
        every { LocaleCache.get(context) } returns ""

        val prefs = preferencesOf()
        every { dataStore.data } returns flowOf(prefs)
        repository = SettingsRepository(dataStore, context)

        val result = repository.settings.first()
        assertEquals("", result.language)
    }

    @Test
    fun `setMusicVolume keeps value within range`() = runTest {
        repository = SettingsRepository(dataStore, context)

        coEvery { dataStore.updateData(any()) } coAnswers {
            val transform = arg<suspend (Preferences) -> Preferences>(0)

            // suspend-Lambda in einem eigenen runBlocking ausführen
            val updated = kotlinx.coroutines.runBlocking {
                transform(emptyPreferences())
            }

            assertEquals(0.5f, updated[SettingsKeys.MUSIC_VOLUME])
            updated
        }
        repository.setMusicVolume(0.5f)

        coVerify { dataStore.updateData(any()) }
    }

    @Test
    fun `setLanguage writes correct key`() = runTest {
        repository = SettingsRepository(dataStore, context)
        coEvery { dataStore.updateData(any()) } coAnswers {
            val transform = arg<suspend (Preferences) -> Preferences>(0)
            val updated = kotlinx.coroutines.runBlocking { transform(emptyPreferences()) }
            assertEquals("fr", updated[SettingsKeys.LANGUAGE])
            updated
        }

        repository.setLanguage("fr")
    }

    @Test
    fun `setMusicVolume clamps value below 0 to 0`() = runTest {
        repository = SettingsRepository(dataStore, context)
        coEvery { dataStore.updateData(any()) } coAnswers {
            val transform = arg<suspend (Preferences) -> Preferences>(0)
            val updated = kotlinx.coroutines.runBlocking { transform(emptyPreferences()) }
            assertEquals(0f, updated[SettingsKeys.MUSIC_VOLUME])
            updated
        }

        repository.setMusicVolume(-1f)
    }

    @Test
    fun `setMusicVolume keeps value at upper bound`() = runTest {
        repository = SettingsRepository(dataStore, context)
        coEvery { dataStore.updateData(any()) } coAnswers {
            val transform = arg<suspend (Preferences) -> Preferences>(0)
            val updated = kotlinx.coroutines.runBlocking { transform(emptyPreferences()) }
            assertEquals(1f, updated[SettingsKeys.MUSIC_VOLUME])
            updated
        }

        repository.setMusicVolume(1f)
    }

    @Test
    fun `settings mixes stored and default values`() = runTest {
        mockkObject(LocaleCache)
        every { LocaleCache.get(context) } returns "en"

        val prefs = preferencesOf(SettingsKeys.LANGUAGE to "de") // nur Sprache gesetzt
        every { dataStore.data } returns flowOf(prefs)
        repository = SettingsRepository(dataStore, context)

        val result = repository.settings.first()

        assertEquals("de", result.language)
        assertEquals(true, result.musicEnabled) // Default
        assertEquals(0.6f, result.musicVolume)  // Default
        assertEquals(true, result.sfxEnabled)   // Default
    }

    @Test
    fun `settings does not call LocaleCache when language is stored`() = runTest {
        mockkObject(LocaleCache)
        every { LocaleCache.get(context) } returns "should_not_be_used"

        val prefs = preferencesOf(SettingsKeys.LANGUAGE to "it")

        every { dataStore.data } returns flowOf(prefs)
        repository = SettingsRepository(dataStore, context)

        val result = repository.settings.first()

        assertEquals("it", result.language)
        verify(exactly = 0) { LocaleCache.get(context) }
    }

    @Test
    fun `settings reads stored musicEnabled true`() = runTest {
        val prefs = preferencesOf(SettingsKeys.MUSIC_ENABLED to true)

        every { dataStore.data } returns flowOf(prefs)
        repository = SettingsRepository(dataStore, context)

        val result = repository.settings.first()
        assertEquals(true, result.musicEnabled)
    }

    @Test
    fun `settings reads stored sfxEnabled false`() = runTest {
        val prefs = preferencesOf(SettingsKeys.SFX_ENABLED to false)

        every { dataStore.data } returns flowOf(prefs)
        repository = SettingsRepository(dataStore, context)

        val result = repository.settings.first()
        assertEquals(false, result.sfxEnabled)
    }
}