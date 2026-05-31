package at.aau.serg.websocketbrokerdemo.ui.settings

import android.app.Activity
import android.app.Application
import at.aau.serg.websocketbrokerdemo.audio.MusicManager
import at.aau.serg.websocketbrokerdemo.data.AppSettings
import at.aau.serg.websocketbrokerdemo.data.LanguageCache
import at.aau.serg.websocketbrokerdemo.data.SettingsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests für SettingsViewModel.
 *
 * Das ViewModel verbindet drei Sachen miteinander:
 *  1. Es liest Settings aus dem Repository und mappt sie zu SettingsState
 *  2. Es delegiert Side-Effects (Locale-Wechsel, Musik) an SettingsLogic
 *  3. Es persistiert Änderungen via Repository
 *
 * Wir mocken das Repository und die beiden Singletons (LocaleCache,
 * MusicManager), die SettingsLogic intern nutzt -- so wird das ViewModel
 * isoliert getestet, ohne dass wir die SettingsLogic separat mocken
 * müssen (das wäre möglich, würde aber den Test enger an die Implementierung
 * koppeln als nötig).
 *
 * Wichtig zum Coroutine-Setup:
 *  - UnconfinedTestDispatcher führt suspend-Aufrufe sofort aus
 *    (kein advanceUntilIdle nötig nach jedem Aufruf, aber sicherheitshalber
 *    drin)
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

        // Repository liefert einen veränderlichen Flow -- so können wir
        // im Test simulieren, dass sich Settings ändern und prüfen, ob
        // das ViewModel das übernimmt.
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
        // Vor dem ersten Collect liefert stateIn den initialValue zurück --
        // das sind die SettingsState-Defaults, nicht die Repository-Defaults.
        // (Die Repository-Defaults werden erst gelesen, wenn jemand den
        // StateFlow tatsächlich beobachtet.)
        val state = viewModel.state.value

        Assertions.assertEquals("en", state.language)
        Assertions.assertEquals(true, state.musicEnabled)
        Assertions.assertEquals(true, state.sfxEnabled)
    }

    // -------------------------------------------------------------------------
    // Flow -> State Mapping
    // -------------------------------------------------------------------------

    @Test
    fun `state mirrors repository settings after collection`() = runTest {
        // Wir simulieren eine Settings-Änderung im Repository und prüfen
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
        // ViewModel hat Initial-State "en". Nochmal "en" auswählen darf
        // nicht recreate() triggern (würde sonst flackern).
        viewModel.onLanguageSelected("en", activity)
        advanceUntilIdle()

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
        // Der Effekt soll sofort hörbar sein, nicht erst nach dem
        // nächsten Settings-Flow-Emit -- daher der direkte MusicManager-Call.
        viewModel.onMusicToggle(false)
        advanceUntilIdle()

        verify { MusicManager.applyMusicSettings(false, any()) }
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
        // Slider-Drag soll live hörbar sein -- die Lautstärke wird sofort
        // an den MusicManager weitergegeben, parallel zum Persistieren.
        viewModel.onVolumeChanged(0.7f)
        advanceUntilIdle()

        verify { MusicManager.applyMusicSettings(any(), 0.7f) }
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
        // UX-Feature: beim Einschalten der Soundeffekte hört der User
        // sofort ein Beispiel-Sound, damit er weiß "ja, ist an".
        viewModel.onSfxToggle(true)
        advanceUntilIdle()

        verify { MusicManager.playSwordBlock() }
    }

    @Test
    fun `onSfxToggle does not play preview when disabling sfx`() = runTest {
        // Symmetrie-Test: beim Ausschalten KEIN Preview, sonst kontra-
        // produktiv ("ich höre noch was, obwohl ich gerade ausgeschaltet hab")
        viewModel.onSfxToggle(false)
        advanceUntilIdle()

        verify(exactly = 0) { MusicManager.playSwordBlock() }
    }
}