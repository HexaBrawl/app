package at.aau.serg.websocketbrokerdemo.ui.settings

import android.app.Activity
import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import at.aau.serg.websocketbrokerdemo.audio.MusicManager
import at.aau.serg.websocketbrokerdemo.data.AppSettings
import at.aau.serg.websocketbrokerdemo.data.LanguageCache
import at.aau.serg.websocketbrokerdemo.data.SettingsRepository
import at.aau.serg.websocketbrokerdemo.data.settingsDataStore
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests fuer SettingsViewModel.
 *
 * Das ViewModel verbindet drei Sachen miteinander:
 *  1. Es liest Settings aus dem Repository und mappt sie zu SettingsState
 *  2. Es delegiert Side-Effects (Locale-Wechsel, Musik) an SettingsLogic
 *  3. Es persistiert Aenderungen via Repository
 *
 * Wir mocken das Repository und die beiden Singletons (LocaleCache,
 * MusicManager), die SettingsLogic intern nutzt -- so wird das ViewModel
 * isoliert getestet, ohne dass wir die SettingsLogic separat mocken
 * muessen (das waere moeglich, wuerde aber den Test enger an die
 * Implementierung koppeln als noetig).
 *
 * Wichtig zum Coroutine-Setup:
 *  - UnconfinedTestDispatcher fuehrt suspend-Aufrufe sofort aus
 *    (kein advanceUntilIdle noetig nach jedem Aufruf, aber sicherheits-
 *    halber drin)
 *  - Dispatchers.setMain ist notwendig, weil viewModelScope intern
 *    Dispatchers.Main.immediate nutzt
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private lateinit var repo: SettingsRepository
    private lateinit var app: Application

    private lateinit var logic: SettingsLogic
    private lateinit var activity: Activity
    private lateinit var viewModel: SettingsViewModel

    private lateinit var settingsFlow: MutableStateFlow<AppSettings>

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Singletons, die SettingsLogic intern aufruft, neutralisieren
        mockkObject(MusicManager)
        mockkObject(LanguageCache)
        every { LanguageCache.set(any(), any()) } returns Unit

        repo = mockk(relaxed = true)
        app = mockk(relaxed = true)
        logic = SettingsLogic(app)
        activity = mockk(relaxed = true)
        justRun { activity.recreate() }

        // Repository liefert einen veraenderlichen Flow -- so koennen wir
        // im Test simulieren, dass sich Settings aendern und pruefen, ob
        // das ViewModel das uebernimmt.
        settingsFlow = MutableStateFlow(AppSettings())
        every { repo.settings } returns settingsFlow

        viewModel = SettingsViewModel(app, repo)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    // -------------------------------------------------------------------------
    // Initial State
    // -------------------------------------------------------------------------

    @Test
    fun `state has default values before flow emits`() {
        // Vor dem ersten Collect liefert stateIn den initialValue zurueck --
        // das sind die SettingsState-Defaults, nicht die Repository-Defaults.
        // (Die Repository-Defaults werden erst gelesen, wenn jemand den
        // StateFlow tatsaechlich beobachtet.)
        val state = viewModel.state.value

        Assertions.assertEquals("en", state.language)
        Assertions.assertEquals(true, state.musicEnabled)
        Assertions.assertEquals(true, state.sfxEnabled)
    }

    // -------------------------------------------------------------------------
    // Sekundaerer Konstruktor
    // -------------------------------------------------------------------------

    @Test
    fun `secondary constructor builds working ViewModel`() {
        // Der secondary constructor (Application only) ist der echte
        // Production-Constructor -- die zwei-Parameter-Variante existiert
        // nur fuer Tests. Hier verifizieren wir dass auch der echte Pfad
        // ein lebensfaehiges ViewModel erzeugt.
        //
        // Trick: settingsDataStore ist eine extension property auf Context,
        // die ueber mockkStatic abgefangen werden kann. Damit umgehen wir
        // das echte DataStore-File-IO.
        mockkStatic("at.aau.serg.websocketbrokerdemo.data.SettingsDataStoreProviderKt")
        val fakeStore = mockk<DataStore<Preferences>>(relaxed = true)
        every { fakeStore.data } returns flowOf(preferencesOf())
        every { any<Application>().settingsDataStore } returns fakeStore

        val vm = SettingsViewModel(app)

        // Das ViewModel sollte initialisiert sein und einen state liefern
        assertNotNull(vm.state)
        assertNotNull(vm.state.value)
    }

    // -------------------------------------------------------------------------
    // Flow -> State Mapping
    // -------------------------------------------------------------------------

    @Test
    fun `state mirrors repository settings after collection`() = runTest {
        // Wir simulieren eine Settings-Aenderung im Repository und pruefen
        // dass das ViewModel sie korrekt nach SettingsState mappt.
        // Wichtig: state muss aktiv collected werden, sonst bleibt es
        // beim initialValue stehen (WhileSubscribed-Verhalten).
        val collectJob = GlobalScope.launch(testDispatcher) {
            viewModel.state.collect { /* aktiv halten */ }
        }
        advanceUntilIdle()

        settingsFlow.value = AppSettings(
            language = "de",
            musicEnabled = false,
            musicVolume = 0.3f,
            sfxEnabled = false
        )
        advanceUntilIdle()

        val state = viewModel.state.value
        Assertions.assertEquals("de", state.language)
        Assertions.assertEquals(false, state.musicEnabled)
        Assertions.assertEquals(0.3f, state.musicVolume, 0.0001f)
        Assertions.assertEquals(30, state.musicVolumePercent)
        Assertions.assertEquals(false, state.sfxEnabled)

        collectJob.cancel()
    }

    @Test
    fun `musicVolumePercent is computed correctly from volume`() = runTest {
        // Edge-Cases der percent-Berechnung: 0% / 100% / krummer Wert.
        // Wichtig weil die UI das Prozent-Label direkt aus dem State nimmt.
        val collectJob = GlobalScope.launch(testDispatcher) {
            viewModel.state.collect { }
        }

        settingsFlow.value = AppSettings(musicVolume = 0f)
        advanceUntilIdle()
        Assertions.assertEquals(0, viewModel.state.value.musicVolumePercent)

        settingsFlow.value = AppSettings(musicVolume = 1f)
        advanceUntilIdle()
        Assertions.assertEquals(100, viewModel.state.value.musicVolumePercent)

        settingsFlow.value = AppSettings(musicVolume = 0.42f)
        advanceUntilIdle()
        // (0.42 * 100).toInt() = 42
        Assertions.assertEquals(42, viewModel.state.value.musicVolumePercent)

        collectJob.cancel()
    }

    @Test
    fun `musicVolume in state matches repository before percent rounding`() = runTest {
        // Sanity: der state speichert den Roh-Float, nicht nur den Prozent-Int.
        // Wichtig damit die UI den Slider auf der korrekten Position rendert.
        val collectJob = GlobalScope.launch(testDispatcher) {
            viewModel.state.collect { }
        }
        settingsFlow.value = AppSettings(musicVolume = 0.337f)
        advanceUntilIdle()

        // Prozent ist gerundet, aber der Float-Wert bleibt exakt
        assertEquals(0.337f, viewModel.state.value.musicVolume, 0.0001f)
        assertEquals(33, viewModel.state.value.musicVolumePercent)

        collectJob.cancel()
    }

    // -------------------------------------------------------------------------
    // onLanguageSelected()
    // -------------------------------------------------------------------------

    @Test
    fun `onLanguageSelected persists language to repository`() = runTest {
        coEvery { repo.updateLanguage("de") } returns Unit

        viewModel.onLanguageSelected("de", activity)
        advanceUntilIdle()

        coVerify { repo.updateLanguage("de") }
    }

    @Test
    fun `onLanguageSelected triggers activity recreate when language changes`() = runTest {
        // Initial-Sprache ist "en" (Default). Bei Wechsel auf "de" muss
        // die Activity neu gestartet werden, damit Compose die neuen
        // String-Resources zieht.
        viewModel.onLanguageSelected("de", activity)
        advanceUntilIdle()

        verify { LanguageCache.set(app, "de") }
        verify { activity.recreate() }
    }

    @Test
    fun `onLanguageSelected with same language does not recreate activity`() = runTest {
        // ViewModel hat Initial-State "en". Nochmal "en" auswaehlen darf
        // nicht recreate() triggern (wuerde sonst flackern).
        viewModel.onLanguageSelected("en", activity)
        advanceUntilIdle()

        verify(exactly = 0) { activity.recreate() }
    }

    @Test
    fun `onLanguageSelected with null activity persists language but does not recreate`() =
        runTest {
            // Edge-Case: LocalContext.current ist nicht immer eine Activity
            // (z. B. in Compose-Previews oder bestimmten Test-Setups).
            // Dann muss die Sprache trotzdem persistiert werden, nur das
            // recreate() entfaellt.
            coEvery { repo.updateLanguage("de") } returns Unit

            viewModel.onLanguageSelected("de", null)
            advanceUntilIdle()

            coVerify { repo.updateLanguage("de") }
            verify { LanguageCache.set(app, "de") }
            // Wichtig: kein recreate() weil keine Activity da
            verify(exactly = 0) { activity.recreate() }
        }

    // -------------------------------------------------------------------------
    // onMusicToggle()
    // -------------------------------------------------------------------------

    @Test
    fun `onMusicToggle persists music enabled to repository`() = runTest {
        coEvery { repo.updateMusicEnabled(false) } returns Unit

        viewModel.onMusicToggle(false)
        advanceUntilIdle()

        coVerify { repo.updateMusicEnabled(false) }
    }

    @Test
    fun `onMusicToggle applies setting to MusicManager immediately`() = runTest {
        // Der Effekt soll sofort hoerbar sein, nicht erst nach dem
        // naechsten Settings-Flow-Emit -- daher der direkte MusicManager-Call.
        viewModel.onMusicToggle(false)
        advanceUntilIdle()

        verify { MusicManager.applyMusicSettings(false, any()) }
    }

    @Test
    fun `onMusicToggle true persists and applies to MusicManager`() = runTest {
        // Symmetrie-Test zum onMusicToggle(false)-Test:
        // auch der true-Pfad muss persistieren UND den MusicManager
        // benachrichtigen.
        coEvery { repo.updateMusicEnabled(true) } returns Unit

        viewModel.onMusicToggle(true)
        advanceUntilIdle()

        coVerify { repo.updateMusicEnabled(true) }
        verify { MusicManager.applyMusicSettings(true, any()) }
    }

    @Test
    fun `onMusicToggle passes current volume from state to MusicManager`() = runTest {
        // Wenn der User Musik an-/ausschaltet, muss der MusicManager mit
        // dem aktuell eingestellten Volume aufgerufen werden -- nicht mit
        // einem hardgecodeten Default.
        settingsFlow.value = AppSettings(musicVolume = 0.42f)
        val collectJob = GlobalScope.launch(testDispatcher) {
            viewModel.state.collect { }
        }
        advanceUntilIdle()

        viewModel.onMusicToggle(true)
        advanceUntilIdle()

        verify { MusicManager.applyMusicSettings(true, 0.42f) }

        collectJob.cancel()
    }

    // -------------------------------------------------------------------------
    // onVolumeChanged()
    // -------------------------------------------------------------------------

    @Test
    fun `onVolumeChanged persists volume to repository`() = runTest {
        coEvery { repo.updateMusicVolume(0.7f) } returns Unit

        viewModel.onVolumeChanged(0.7f)
        advanceUntilIdle()

        coVerify { repo.updateMusicVolume(0.7f) }
    }

    @Test
    fun `onVolumeChanged applies volume to MusicManager immediately`() = runTest {
        // Slider-Drag soll live hoerbar sein -- die Lautstaerke wird sofort
        // an den MusicManager weitergegeben, parallel zum Persistieren.
        viewModel.onVolumeChanged(0.7f)
        advanceUntilIdle()

        verify { MusicManager.applyMusicSettings(any(), 0.7f) }
    }

    @Test
    fun `onVolumeChanged accepts boundary value 0`() = runTest {
        // Minimaler Volume-Wert; haeufig genutzt zum "stummschalten" via Slider
        coEvery { repo.updateMusicVolume(0f) } returns Unit

        viewModel.onVolumeChanged(0f)
        advanceUntilIdle()

        coVerify { repo.updateMusicVolume(0f) }
        verify { MusicManager.applyMusicSettings(any(), 0f) }
    }

    @Test
    fun `onVolumeChanged accepts boundary value 1`() = runTest {
        // Maximaler Volume-Wert
        coEvery { repo.updateMusicVolume(1f) } returns Unit

        viewModel.onVolumeChanged(1f)
        advanceUntilIdle()

        coVerify { repo.updateMusicVolume(1f) }
        verify { MusicManager.applyMusicSettings(any(), 1f) }
    }

    @Test
    fun `onVolumeChanged passes current musicEnabled from state to MusicManager`() = runTest {
        // Symmetrisch: Slider bewegen muss MusicManager mit dem aktuellen
        // enabled-Status (an oder aus) aufrufen.
        settingsFlow.value = AppSettings(musicEnabled = false, musicVolume = 0.5f)
        val collectJob = GlobalScope.launch(testDispatcher) {
            viewModel.state.collect { }
        }
        advanceUntilIdle()

        viewModel.onVolumeChanged(0.8f)
        advanceUntilIdle()

        verify { MusicManager.applyMusicSettings(false, 0.8f) }

        collectJob.cancel()
    }

    // -------------------------------------------------------------------------
    // onSfxToggle()
    // -------------------------------------------------------------------------

    @Test
    fun `onSfxToggle persists sfx setting to repository`() = runTest {
        coEvery { repo.updateSfxEnabled(false) } returns Unit

        viewModel.onSfxToggle(false)
        advanceUntilIdle()

        coVerify { repo.updateSfxEnabled(false) }
    }

    @Test
    fun `onSfxToggle applies setting to MusicManager`() = runTest {
        viewModel.onSfxToggle(true)
        advanceUntilIdle()

        verify { MusicManager.applySfxSettings(true) }
    }

    @Test
    fun `onSfxToggle plays preview sound when enabling sfx`() = runTest {
        // UX-Feature: beim Einschalten der Soundeffekte hoert der User
        // sofort ein Beispiel-Sound, damit er weiss "ja, ist an".
        viewModel.onSfxToggle(true)
        advanceUntilIdle()

        verify { MusicManager.playSwordBlock() }
    }

    @Test
    fun `onSfxToggle does not play preview when disabling sfx`() = runTest {
        // Symmetrie-Test: beim Ausschalten KEIN Preview, sonst kontra-
        // produktiv ("ich hoere noch was, obwohl ich gerade ausgeschaltet hab")
        viewModel.onSfxToggle(false)
        advanceUntilIdle()

        verify(exactly = 0) { MusicManager.playSwordBlock() }
    }
}