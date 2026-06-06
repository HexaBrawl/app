package at.aau.serg.websocketbrokerdemo.ui.game

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Tests fuer GameHudSizingLogic.
 *
 * Pruefen dass:
 *  - Referenz-Breite (S21 Ultra) den manuell abgestimmten 1.0-Scale ergibt
 *  - kleinere Bildschirme verkleinerte Werte bekommen
 *  - groessere Bildschirme groessere Werte bekommen
 *  - Min/Max-Clamping greift bei Extremen
 *  - Schrift bleibt lesbar
 */
class GameHudSizingTest {

    @Test
    fun `reference width gives the manually tuned values`() {
        val sizing = GameHudSizingLogic.forWidth(384f)
        assertEquals(50f, sizing.bottomIconSize.value, 0.01f)
        assertEquals(100f, sizing.bottomBarHeight.value, 0.01f)
        assertEquals(12f, sizing.bottomTitleFontSize.value, 0.01f)
        assertEquals(16f, sizing.bottomCoinPriceFontSize.value, 0.01f)
        assertEquals(40f, sizing.topGoldIconSize.value, 0.01f)
        assertEquals(24f, sizing.topGoldFontSize.value, 0.01f)
        assertEquals(52f, sizing.topMenuButtonSize.value, 0.01f)
    }

    @Test
    fun `smaller screen gets smaller bottom hud values`() {
        val small = GameHudSizingLogic.forWidth(320f)
        val ref = GameHudSizingLogic.forWidth(384f)
        assertTrue(small.bottomIconSize.value < ref.bottomIconSize.value)
        assertTrue(small.bottomBarHeight.value < ref.bottomBarHeight.value)
        assertTrue(small.bottomCoinSpacing.value < ref.bottomCoinSpacing.value)
    }

    @Test
    fun `smaller screen gets smaller top hud values`() {
        val small = GameHudSizingLogic.forWidth(320f)
        val ref = GameHudSizingLogic.forWidth(384f)
        assertTrue(small.topGoldIconSize.value < ref.topGoldIconSize.value)
        assertTrue(small.topMenuButtonSize.value < ref.topMenuButtonSize.value)
    }

    @Test
    fun `larger screen gets larger values`() {
        val large = GameHudSizingLogic.forWidth(480f)
        val ref = GameHudSizingLogic.forWidth(384f)
        assertTrue(large.bottomIconSize.value > ref.bottomIconSize.value)
        assertTrue(large.topGoldFontSize.value > ref.topGoldFontSize.value)
    }

    @Test
    fun `min scale clamp kicks in for very small screens`() {
        val tiny = GameHudSizingLogic.forWidth(240f)
        val expectedMin = 50f * 0.75f
        assertEquals(expectedMin, tiny.bottomIconSize.value, 0.01f)
    }

    @Test
    fun `max scale clamp kicks in for very large screens`() {
        val huge = GameHudSizingLogic.forWidth(800f)
        val expectedMax = 50f * 1.25f
        assertEquals(expectedMax, huge.bottomIconSize.value, 0.01f)
    }

    @Test
    fun `title font stays readable on smallest screens`() {
        val tiny = GameHudSizingLogic.forWidth(240f)
        assertTrue(tiny.bottomTitleFontSize.value >= 10f)
    }

    @Test
    fun `large coin price stays readable on smallest screens`() {
        val tiny = GameHudSizingLogic.forWidth(240f)
        assertTrue(tiny.bottomCoinPriceFontSize.value >= 12f)
    }

    @Test
    fun `gold font stays readable on smallest screens`() {
        val tiny = GameHudSizingLogic.forWidth(240f)
        assertTrue(tiny.topGoldFontSize.value >= 14f)
    }

    @Test
    fun `default sizing matches reference width`() {
        val ref = GameHudSizingLogic.forWidth(384f)
        assertEquals(ref, GameHudSizing.Default)
    }

    @Test
    fun `bottom bar side padding is fixed at 3dp`() {
        val small = GameHudSizingLogic.forWidth(320f)
        val ref = GameHudSizingLogic.forWidth(384f)
        val large = GameHudSizingLogic.forWidth(480f)
        assertEquals(3f, small.bottomBarSidePadding.value, 0.01f)
        assertEquals(3f, ref.bottomBarSidePadding.value, 0.01f)
        assertEquals(3f, large.bottomBarSidePadding.value, 0.01f)
    }
}