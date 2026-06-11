package at.aau.serg.websocketbrokerdemo.ui.mainmenu

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import at.aau.serg.websocketbrokerdemo.data.serverside.GameMode as ServerGameMode

/**
 * Tests fuer das Server-zu-UI GameMode-Mapping.
 *
 * Pure JVM-Tests -- die Mapping-Funktion ist eine reine Extension
 * ohne Compose- oder Android-Abhaengigkeiten, damit hier sauber
 * abgesichert werden kann, dass jeder Server-Modus dem richtigen
 * UI-Modus zugeordnet wird.
 */
class GameModeMappingTest {

    @Test
    fun `DUAL_VALLEY maps to DUAL_VALLEY`() {
        assertEquals(GameMode.DUAL_VALLEY, ServerGameMode.DUAL_VALLEY.toUiMode())
    }

    @Test
    fun `TRIAD_OUTPOST maps to TRIAD_OUTPOST`() {
        assertEquals(GameMode.TRIAD_OUTPOST, ServerGameMode.TRIAD_OUTPOST.toUiMode())
    }

    @Test
    fun `BATTLEFIELD_PEAKS maps to BATTLEFIELD_PEAKS`() {
        assertEquals(GameMode.BATTLEFIELD_PEAKS, ServerGameMode.BATTLEFIELD_PEAKS.toUiMode())
    }
}
