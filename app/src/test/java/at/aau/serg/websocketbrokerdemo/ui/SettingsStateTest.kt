package at.aau.serg.websocketbrokerdemo.ui.settings

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Tests für die UI-Datenklasse SettingsState.
 *
 * Hier wird nichts mit Coroutinen/Mocks gemacht -- nur sichergestellt, dass
 * die Defaults stimmen, copy() das tut was es soll, und equals/hashCode
 * data-class-konform sind. Das schützt die UI vor versehentlichen
 * Default-Änderungen, die sonst zu komischen Verhalten im SettingsScreen
 * führen könnten.
 */
class SettingsStateTest {

    @Test
    fun `default state matches the documented defaults`() {
        // Soll: en, Musik an, Volume 1.0 (100%), SFX an.
        // Wenn jemand diese Defaults ändert, sollte er das bewusst tun --
        // dieser Test fängt versehentliche Änderungen.
        val state = SettingsState()

        assertEquals("en", state.language)
        assertTrue(state.musicEnabled)
        assertEquals(1f, state.musicVolume, 0.0001f)
        assertEquals(100, state.musicVolumePercent)
        assertTrue(state.sfxEnabled)
    }

    @Test
    fun `copy preserves untouched fields`() {
        // copy() ist die Hauptweise, wie das ViewModel den State aktualisiert.
        // Hier prüfen wir, dass nur das geänderte Feld wechselt.
        val original = SettingsState(language = "de", musicVolume = 0.4f)
        val updated = original.copy(musicEnabled = false)

        assertEquals("de", updated.language)
        assertEquals(0.4f, updated.musicVolume, 0.0001f)
        assertFalse(updated.musicEnabled)
        assertTrue(updated.sfxEnabled)
    }

    @Test
    fun `equals returns true for identical states`() {
        // data-class equals: zwei Instanzen mit gleichen Feldern sind gleich.
        // Wichtig für StateFlow-Distinct-Behaviour.
        val a = SettingsState(language = "de", musicEnabled = false, musicVolume = 0.5f, musicVolumePercent = 50, sfxEnabled = false)
        val b = SettingsState(language = "de", musicEnabled = false, musicVolume = 0.5f, musicVolumePercent = 50, sfxEnabled = false)

        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `equals returns false when language differs`() {
        // Sanity-Check: data-class-equals reagiert auf jedes einzelne Feld.
        val a = SettingsState(language = "de")
        val b = SettingsState(language = "en")

        assertNotEquals(a, b)
    }

    @Test
    fun `musicVolumePercent is independent from musicVolume`() {
        // Achtung: musicVolumePercent ist ein eigenes Feld -- es wird im
        // ViewModel manuell aus musicVolume berechnet. Dieser Test
        // dokumentiert das aktuelle Verhalten: die Datenklasse erzwingt
        // keine Konsistenz zwischen volume und percent.
        val state = SettingsState(musicVolume = 0.3f, musicVolumePercent = 99)

        assertEquals(0.3f, state.musicVolume, 0.0001f)
        assertEquals(99, state.musicVolumePercent)
    }
}
