package at.aau.serg.websocketbrokerdemo.data

import android.content.Context
import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Locale

class LanguageCacheTest {

    private lateinit var context: Context
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var savedDefault: Locale = Locale.getDefault()

    @BeforeEach
    fun setUp() {
        savedDefault = Locale.getDefault()

        context = mockk(relaxed = true)
        prefs = mockk(relaxed = true)
        editor = mockk(relaxed = true)

        every { context.getSharedPreferences(any(), any()) } returns prefs
        every { prefs.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor
    }

    @AfterEach
    fun tearDown() {
        Locale.setDefault(savedDefault)
    }

    @Test
    fun `get returns stored value if present`() {
        every { prefs.getString("language", any()) } returns "de"

        val result = LanguageCache.get(context)

        assertEquals("de", result)
    }

    @Test
    fun `get returns english as fallback when system is not german`() {
        Locale.setDefault(Locale.FRANCE)
        // SharedPreferences signalisiert "kein Wert gespeichert" -> Default greift
        val defaultSlot = slot<String>()
        every { prefs.getString(eq("language"), capture(defaultSlot)) } answers { defaultSlot.captured }

        val result = LanguageCache.get(context)

        assertEquals("en", result)
    }

    @Test
    fun `get returns german as fallback when system is german`() {
        Locale.setDefault(Locale.GERMAN)
        val defaultSlot = slot<String>()
        every { prefs.getString(eq("language"), capture(defaultSlot)) } answers { defaultSlot.captured }

        val result = LanguageCache.get(context)

        assertEquals("de", result)
    }

    @Test
    fun `set writes language and applies`() {
        LanguageCache.set(context, "de")

        verify { editor.putString("language", "de") }
        verify { editor.apply() }
    }

    @Test
    fun `set uses MODE_PRIVATE`() {
        LanguageCache.set(context, "en")

        verify { context.getSharedPreferences("hexabrawl_locale_cache", Context.MODE_PRIVATE) }
    }

    @Test
    fun `get uses correct prefs name`() {
        every { prefs.getString(any(), any()) } returns null

        LanguageCache.get(context)

        verify { context.getSharedPreferences("hexabrawl_locale_cache", Context.MODE_PRIVATE) }
    }

    @Test
    fun `get returns en when prefs returns null and system locale is unknown`() {
        Locale.setDefault(Locale.JAPAN)
        every { prefs.getString(any(), any()) } returns null

        val result = LanguageCache.get(context)

        assertEquals("en", result)
    }
}
