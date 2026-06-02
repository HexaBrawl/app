package at.aau.serg.websocketbrokerdemo.ui.settings

import android.app.Activity
import android.app.Application
import at.aau.serg.websocketbrokerdemo.audio.MusicManager
import at.aau.serg.websocketbrokerdemo.data.LanguageCache
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests für SettingsLogic.
 *
 * SettingsLogic ist die extrahierte Glue-Klasse zwischen ViewModel und den
 * Side-Effect-Singletons (LocaleCache, MusicManager). Sie hat keine eigene
 * State-Verwaltung -- jeder Aufruf delegiert direkt weiter.
 *
 * Wir testen vor allem Branching-Logik (z.B. "kein recreate wenn dieselbe
 * Sprache schon aktiv ist") und das korrekte Forwarding an die Singletons.
 *
 * Mocking:
 *  - Application + Activity -> normale Mocks (kein Lifecycle nötig)
 *  - LocaleCache + MusicManager sind Kotlin-objects -> mockkObject
 */
class SettingsLogicTest {

    private lateinit var app: Application
    private lateinit var activity: Activity
    private lateinit var logic: SettingsLogic

    @BeforeEach
    fun setUp() {
        app = mockk(relaxed = true)
        activity = mockk(relaxed = true)

        mockkObject(LanguageCache)
        mockkObject(MusicManager)

        every { LanguageCache.set(any(), any()) } returns Unit
        justRun { activity.recreate() }

        logic = SettingsLogic(app)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    // -------------------------------------------------------------------------
    // changeLanguage()
    // -------------------------------------------------------------------------

    @Test
    fun `changeLanguage updates cache and recreates activity when language changes`() {
        // Klassischer Pfad: aktuelle Sprache != neue Sprache -> cache schreiben
        // und Activity neu starten, damit Compose die neuen Strings zieht.
        logic.changeLanguage(currentLang = "en", newLang = "de", activity = activity)

        verify { LanguageCache.set(app, "de") }
        verify { activity.recreate() }
    }

    @Test
    fun `changeLanguage does nothing when current equals new`() {
        // Optimierung: gleiche Sprache zweimal anklicken soll NICHT die
        // Activity neu starten (sonst gibt's Flackern und Backstack-Verlust).
        logic.changeLanguage(currentLang = "de", newLang = "de", activity = activity)

        verify(exactly = 0) { LanguageCache.set(any(), any()) }
        verify(exactly = 0) { activity.recreate() }
    }

    @Test
    fun `changeLanguage handles null activity gracefully`() {
        // Wenn der Context kein Activity ist (sollte selten passieren, aber
        // möglich bei Tests/Compose-Previews), darf nichts crashen.
        // Cache wird trotzdem geschrieben, recreate() entfällt einfach.
        logic.changeLanguage(currentLang = "en", newLang = "de", activity = null)

        verify { LanguageCache.set(app, "de") }
        // Kein verify auf recreate() -- es gibt keine Activity dafür.
    }

    // -------------------------------------------------------------------------
    // applyMusicSettings()
    // -------------------------------------------------------------------------

    @Test
    fun `applyMusicSettings forwards both parameters to MusicManager`() {
        // Reines Forwarding -- wir prüfen nur dass die richtigen Werte
        // an den Singleton durchgereicht werden.
        logic.applyMusicSettings(enabled = true, volume = 0.7f)

        verify { MusicManager.applyMusicSettings(true, 0.7f) }
    }

    @Test
    fun `applyMusicSettings works with music disabled`() {
        // Auch der disabled-Case wird durchgereicht (MusicManager
        // entscheidet selbst was zu tun ist).
        logic.applyMusicSettings(enabled = false, volume = 0.5f)

        verify { MusicManager.applyMusicSettings(false, 0.5f) }
    }

    // -------------------------------------------------------------------------
    // applySfxSettings()
    // -------------------------------------------------------------------------

    @Test
    fun `applySfxSettings enabled forwards true`() {
        logic.applySfxSettings(enabled = true)
        verify { MusicManager.applySfxSettings(true) }
    }

    @Test
    fun `applySfxSettings disabled forwards false`() {
        logic.applySfxSettings(enabled = false)
        verify { MusicManager.applySfxSettings(false) }
    }

    // -------------------------------------------------------------------------
    // playSfxIfEnabled()
    // -------------------------------------------------------------------------

    @Test
    fun `playSfxIfEnabled plays sword block sound when enabled`() {
        // Beim Aktivieren des SFX-Switches im SettingsScreen wird ein
        // Audio-Preview abgespielt. Hier prüfen wir dass das wirklich
        // beim Aktivieren passiert.
        logic.playSfxIfEnabled(enabled = true)

        verify { MusicManager.playSwordBlock() }
    }

    @Test
    fun `playSfxIfEnabled stays silent when disabled`() {
        // Wichtig: beim DEAKTIVIEREN soll KEIN Preview abgespielt werden,
        // sonst hört der User noch genau das Geräusch, das er gerade
        // ausschaltet -- verwirrend.
        logic.playSfxIfEnabled(enabled = false)

        verify(exactly = 0) { MusicManager.playSwordBlock() }
    }
}