package at.aau.serg.websocketbrokerdemo.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

/**
 * Tests für SettingsRepository.
 *
 * Strategie:
 *  - Statt DataStore zu mocken, verwenden wir einen ECHTEN
 *    PreferenceDataStore in einem temporären Verzeichnis. Das ist robuster
 *    als das Mocken von suspend-Extension-Funktionen wie `edit { }`, die
 *    intern komplexe Coroutine-Logik haben.
 *  - Wir mocken nur die Top-Level-Extension Context.dataStore (damit
 *    SettingsRepository unseren Test-DataStore statt des "echten" sieht)
 *    und LocaleCache (object).
 *
 * Voraussetzung in SettingsRepository.kt:
 *     internal val Context.dataStore: DataStore<Preferences> by preferencesDataStore(...)
 */
class SettingsRepositoryTest {

    @TempDir
    lateinit var tempDir: File

    private lateinit var context: Context
    private lateinit var dataStore: DataStore<Preferences>

    @BeforeEach
    fun setUp() {
        context = mockk(relaxed = true)

        // Ein echter, in einem TempDir gespeicherter DataStore.
        // Eindeutiger Filename pro Testlauf, damit Tests sich nicht stören.
        val file = File(tempDir, "settings_${System.nanoTime()}.preferences_pb")
        dataStore = PreferenceDataStoreFactory.create(produceFile = { file })

        // Context.dataStore-Extension umlenken auf unseren Test-DataStore
        mockkStatic("at.aau.serg.websocketbrokerdemo.data.SettingsDataStoreProviderKt")
        every { any<Context>().settingsDataStore } returns dataStore

        // LocaleCache als object mocken
        mockkObject(LanguageCache)
        every { LanguageCache.set(any(), any()) } returns Unit
        every { LanguageCache.get(any()) } returns "en"
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    // ---- settings flow --------------------------------------------------

    @Test
    fun `settings flow returns defaults when DataStore is empty`() = runTest {
        val result = SettingsRepository(dataStore, context).settings.first()

        assertEquals("en", result.language)
        assertTrue(result.musicEnabled)
        assertEquals(0.6f, result.musicVolume, 0.0001f)
        assertTrue(result.sfxEnabled)
    }

    @Test
    fun `settings flow returns stored language`() = runTest {
        // Zuerst über das Repository den Wert schreiben
        val repo = SettingsRepository(dataStore, context)
        repo.updateLanguage("de")

        // Dann lesen
        val result = repo.settings.first()
        assertEquals("de", result.language)
    }

    @Test
    fun `settings flow returns stored musicEnabled false`() = runTest {
        val repo = SettingsRepository(dataStore, context)
        repo.updateMusicEnabled(false)

        val result = repo.settings.first()
        assertFalse(result.musicEnabled)
    }

    @Test
    fun `settings flow returns stored musicVolume`() = runTest {
        val repo = SettingsRepository(dataStore, context)
        repo.updateMusicVolume(0.25f)

        val result = repo.settings.first()
        assertEquals(0.25f, result.musicVolume, 0.0001f)
    }

    @Test
    fun `settings flow returns stored sfxEnabled false`() = runTest {
        val repo = SettingsRepository(dataStore, context)
        repo.updateSfxEnabled(false)

        val result = repo.settings.first()
        assertFalse(result.sfxEnabled)
    }

    @Test
    fun `settings flow returns all stored values together`() = runTest {
        val repo = SettingsRepository(dataStore, context)
        repo.updateLanguage("de")
        repo.updateMusicEnabled(false)
        repo.updateMusicVolume(0.3f)
        repo.updateSfxEnabled(false)

        val result = repo.settings.first()
        assertEquals("de", result.language)
        assertFalse(result.musicEnabled)
        assertEquals(0.3f, result.musicVolume, 0.0001f)
        assertFalse(result.sfxEnabled)
    }

    @Test
    fun `settings flow language falls back to LocaleCache when not stored`() = runTest {
        every { LanguageCache.get(any()) } returns "de"

        // DataStore ist leer -> Fallback auf LocaleCache
        val result = SettingsRepository(dataStore, context).settings.first()

        assertEquals("de", result.language)
        verify { LanguageCache.get(any()) }
    }

    // ---- setLanguage ----------------------------------------------------

    @Test
    fun `setLanguage persists value to DataStore`() = runTest {
        val repo = SettingsRepository(dataStore, context)

        repo.updateLanguage("de")

        // Nach setLanguage muss der Wert im DataStore stehen
        val prefs = dataStore.data.first()
        assertEquals("de", prefs[SettingsKeys.LANGUAGE])
    }

    @Test
    fun `setLanguage also updates LocaleCache`() = runTest {
        SettingsRepository(dataStore, context).updateLanguage("de")

        verify { LanguageCache.set(context, "de") }
    }

    @Test
    fun `setLanguage stores correct value in preferences`() = runTest {
        SettingsRepository(dataStore, context).updateLanguage("de")

        val prefs = dataStore.data.first()
        assertEquals("de", prefs[SettingsKeys.LANGUAGE])
    }

    // ---- setMusicEnabled ------------------------------------------------

    @Test
    fun `setMusicEnabled writes value to preferences`() = runTest {
        SettingsRepository(dataStore, context).updateMusicEnabled(false)

        val prefs = dataStore.data.first()
        assertEquals(false, prefs[SettingsKeys.MUSIC_ENABLED])
    }

    // ---- setMusicVolume -------------------------------------------------

    @Test
    fun `setMusicVolume stores value within range`() = runTest {
        SettingsRepository(dataStore, context).updateMusicVolume(0.42f)

        val prefs = dataStore.data.first()
        assertEquals(0.42f, prefs[SettingsKeys.MUSIC_VOLUME])
    }

    @Test
    fun `setMusicVolume coerces values above 1 down to 1`() = runTest {
        SettingsRepository(dataStore, context).updateMusicVolume(1.5f)

        val prefs = dataStore.data.first()
        assertEquals(1.0f, prefs[SettingsKeys.MUSIC_VOLUME])
    }

    @Test
    fun `setMusicVolume coerces negative values up to 0`() = runTest {
        SettingsRepository(dataStore, context).updateMusicVolume(-0.5f)

        val prefs = dataStore.data.first()
        assertEquals(0.0f, prefs[SettingsKeys.MUSIC_VOLUME])
    }

    @Test
    fun `setMusicVolume accepts boundary 0`() = runTest {
        SettingsRepository(dataStore, context).updateMusicVolume(0f)

        val prefs = dataStore.data.first()
        assertEquals(0.0f, prefs[SettingsKeys.MUSIC_VOLUME])
    }

    @Test
    fun `setMusicVolume accepts boundary 1`() = runTest {
        SettingsRepository(dataStore, context).updateMusicVolume(1f)

        val prefs = dataStore.data.first()
        assertEquals(1.0f, prefs[SettingsKeys.MUSIC_VOLUME])
    }

    // ---- setSfxEnabled --------------------------------------------------

    @Test
    fun `setSfxEnabled writes value to preferences`() = runTest {
        SettingsRepository(dataStore, context).updateSfxEnabled(false)

        val prefs = dataStore.data.first()
        assertEquals(false, prefs[SettingsKeys.SFX_ENABLED])
    }
}