package at.aau.serg.websocketbrokerdemo.ui.game

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class GameHudSizingLogicTest
{
    // Toleranz fuer Float-Vergleiche
    private val delta = 0.1f

    // --- Scale-Clamping ---

    @Test
    fun `reference width produces scale factor 1`() {
        val sizing = GameHudSizingLogic.forWidth(384f)
        assertEquals(100f, sizing.bottomBarHeight.value, delta)
        assertEquals(50f, sizing.bottomIconSize.value, delta)
    }

    @Test
    fun `small screen clamps to minimum scale 0_75`() {
        val sizing = GameHudSizingLogic.forWidth(100f)
        assertEquals(75f, sizing.bottomBarHeight.value, delta)
        assertEquals(37.5f, sizing.bottomIconSize.value, delta)
    }

    @Test
    fun `large screen clamps to maximum scale 1_25`() {
        val sizing = GameHudSizingLogic.forWidth(1000f)
        assertEquals(125f, sizing.bottomBarHeight.value, delta)
        assertEquals(62.5f, sizing.bottomIconSize.value, delta)
    }

    // --- Font-Floor ---

    @Test
    fun `title font never goes below minimum on tiny screen`() {
        val sizing = GameHudSizingLogic.forWidth(100f)
        assert(sizing.bottomTitleFontSize.value >= 10f)
    }

    @Test
    fun `gold font never goes below minimum on tiny screen`() {
        val sizing = GameHudSizingLogic.forWidth(100f)
        assert(sizing.topGoldFontSize.value >= 14f)
    }

    // --- Side Padding ist fix ---

    @Test
    fun `bottomBarSidePadding is always 3dp regardless of screen size`() {
        val small = GameHudSizingLogic.forWidth(100f)
        val large = GameHudSizingLogic.forWidth(1000f)
        assertEquals(3f, small.bottomBarSidePadding.value, delta)
        assertEquals(3f, large.bottomBarSidePadding.value, delta)
    }

    // --- Proportionalitaet ---

    @Test
    fun `larger screen produces larger icon than smaller screen`() {
        val small = GameHudSizingLogic.forWidth(300f)
        val large = GameHudSizingLogic.forWidth(500f)
        assert(large.bottomIconSize.value > small.bottomIconSize.value)
    }
}