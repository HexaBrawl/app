package at.aau.serg.websocketbrokerdemo.ui

import android.app.Application
import at.aau.serg.websocketbrokerdemo.audio.MusicManager
import at.aau.serg.websocketbrokerdemo.data.AppSettings
import at.aau.serg.websocketbrokerdemo.data.SettingsRepository
import at.aau.serg.websocketbrokerdemo.ui.settings.SettingsViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private lateinit var repo: SettingsRepository
    private lateinit var app: Application
    private lateinit var viewModel: SettingsViewModel

    private lateinit var settingsFlow: MutableStateFlow<AppSettings>

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkObject(MusicManager)

        // Mock Repository + Application
        repo = mockk(relaxed = true)
        app = mockk(relaxed = true)

        // Fake Flow für Settings
        settingsFlow = MutableStateFlow(AppSettings())
        every { repo.settings } returns settingsFlow

        // ViewModel mit Mock-Repo
        viewModel = SettingsViewModel(app, repo)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `setLanguage calls repository`() = runTest {
        coEvery { repo.setLanguage("de") } returns Unit

        viewModel.setLanguage("de")
        advanceUntilIdle()

        coVerify { repo.setLanguage("de") }
    }

    @Test
    fun `setMusicEnabled calls repository`() = runTest {
        coEvery { repo.setMusicEnabled(true) } returns Unit

        viewModel.setMusicEnabled(true)
        advanceUntilIdle()

        coVerify { repo.setMusicEnabled(true) }
    }

    @Test
    fun `setMusicVolume calls repository`() = runTest {
        coEvery { repo.setMusicVolume(0.7f) } returns Unit

        viewModel.setMusicVolume(0.7f)
        advanceUntilIdle()

        coVerify { repo.setMusicVolume(0.7f) }
    }

    @Test
    fun `setSfxEnabled calls repository`() = runTest {
        coEvery { repo.setSfxEnabled(false) } returns Unit

        viewModel.setSfxEnabled(false)
        advanceUntilIdle()

        coVerify { repo.setSfxEnabled(false) }
    }

    @Test
    fun `settings flow has correct initial value`() {
        assert(viewModel.settings.value == AppSettings())
    }
}