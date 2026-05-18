package at.aau.serg.websocketbrokerdemo.ui

import android.app.Application
import at.aau.serg.websocketbrokerdemo.data.AppSettings
import at.aau.serg.websocketbrokerdemo.data.SettingsRepository
import at.aau.serg.websocketbrokerdemo.ui.settings.SettingsViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private lateinit var repo: SettingsRepository
    private lateinit var app: Application
    private lateinit var viewModel: SettingsViewModel

    private lateinit var settingsFlow: MutableStateFlow<AppSettings>

    @BeforeEach
    fun setup() {
        // Mock Repository + Application
        repo = mockk(relaxed = true)
        app = mockk(relaxed = true)

        // Fake Flow für Settings
        settingsFlow = MutableStateFlow(AppSettings())
        every { repo.settings } returns settingsFlow

        // ViewModel mit Mock-Repo
        viewModel = SettingsViewModel(app, repo)
    }

    @Test
    fun `setLanguage calls repository`() = runTest {
        coEvery { repo.setLanguage("de") } returns Unit

        viewModel.setLanguage("de")

        coVerify { repo.setLanguage("de") }
    }

    @Test
    fun `setMusicEnabled calls repository`() = runTest {
        coEvery { repo.setMusicEnabled(true) } returns Unit

        viewModel.setMusicEnabled(true)

        coVerify { repo.setMusicEnabled(true) }
    }

    @Test
    fun `setMusicVolume calls repository`() = runTest {
        coEvery { repo.setMusicVolume(0.7f) } returns Unit

        viewModel.setMusicVolume(0.7f)

        coVerify { repo.setMusicVolume(0.7f) }
    }

    @Test
    fun `setSfxEnabled calls repository`() = runTest {
        coEvery { repo.setSfxEnabled(false) } returns Unit

        viewModel.setSfxEnabled(false)

        coVerify { repo.setSfxEnabled(false) }
    }

    @Test
    fun `settings flow has correct initial value`() {
        assert(viewModel.settings.value == AppSettings())
    }
}