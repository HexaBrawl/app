package at.aau.serg.websocketbrokerdemo.data

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.slot
import io.mockk.unmockkConstructor
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Locale

/**
 * Tests für LocaleHelper.
 *
 * Hintergrund zum Setup: In Android-Unit-Tests sind alle `android.*`-
 * Methoden standardmäßig "not mocked" und werfen RuntimeException. Wir
 * können nicht eine echte Configuration-Instanz nutzen, sondern müssen
 * - den Configuration-Konstruktor mocken (mockkConstructor)
 * - die setLocale/setLayoutDirection-Aufrufe abfangen
 *
 * Die übergebene Locale fangen wir mit slot() ab und prüfen sie direkt –
 * das deckt die Sprach-Normalisierung vollständig ab, ohne dass wir auf
 * echte Android-APIs angewiesen sind.
 */
class LanguageHelperTest {

    private lateinit var context: Context
    private lateinit var resources: Resources
    private lateinit var existingConfig: Configuration
    private lateinit var wrappedContext: Context
    private var savedDefault: Locale = Locale.getDefault()

    @BeforeEach
    fun setUp() {
        savedDefault = Locale.getDefault()

        // Configuration ist eine final Klasse mit Android-Methoden ->
        // mockkConstructor fängt das `Configuration(other)` im Helper ab.
        mockkConstructor(Configuration::class)
        every { anyConstructed<Configuration>().setLocale(any()) } returns Unit
        every { anyConstructed<Configuration>().setLayoutDirection(any()) } returns Unit

        context = mockk(relaxed = true)
        resources = mockk(relaxed = true)
        existingConfig = mockk(relaxed = true)
        wrappedContext = mockk(relaxed = true)

        every { context.resources } returns resources
        every { resources.configuration } returns existingConfig
        every { context.createConfigurationContext(any()) } returns wrappedContext
    }

    @AfterEach
    fun tearDown() {
        unmockkConstructor(Configuration::class)
        Locale.setDefault(savedDefault)
    }

    // -------------------------------------------------------------------------
    // Locale-Normalisierung (über Locale.getDefault verifiziert)
    // -------------------------------------------------------------------------

    @Test
    fun `updateLocale with de sets german locale`() {
        LanguageHelper.updateLocale(context, "de")
        assertEquals("de", Locale.getDefault().language)
    }

    @Test
    fun `updateLocale with en sets english locale`() {
        LanguageHelper.updateLocale(context, "en")
        assertEquals("en", Locale.getDefault().language)
    }

    @Test
    fun `updateLocale with unknown language falls back to en`() {
        LanguageHelper.updateLocale(context, "fr")
        assertEquals("en", Locale.getDefault().language)
    }

    @Test
    fun `updateLocale with empty string falls back to en`() {
        LanguageHelper.updateLocale(context, "")
        assertEquals("en", Locale.getDefault().language)
    }

    // -------------------------------------------------------------------------
    // Verhalten gegenüber Context / Configuration
    // -------------------------------------------------------------------------

    @Test
    fun `updateLocale returns the wrapped context`() {
        val result = LanguageHelper.updateLocale(context, "de")
        assertEquals(wrappedContext, result)
    }

    @Test
    fun `updateLocale invokes context createConfigurationContext exactly once`() {
        LanguageHelper.updateLocale(context, "en")
        verify(exactly = 1) { context.createConfigurationContext(any()) }
    }

    @Test
    fun `updateLocale applies the locale to the configuration`() {
        val localeSlot = slot<Locale>()
        every { anyConstructed<Configuration>().setLocale(capture(localeSlot)) } returns Unit

        LanguageHelper.updateLocale(context, "de")

        // Die übergebene Locale trägt die deutsche Sprache
        assertEquals("de", localeSlot.captured.language)
    }

    @Test
    fun `updateLocale also sets layout direction`() {
        LanguageHelper.updateLocale(context, "en")
        verify { anyConstructed<Configuration>().setLayoutDirection(any()) }
    }
}
