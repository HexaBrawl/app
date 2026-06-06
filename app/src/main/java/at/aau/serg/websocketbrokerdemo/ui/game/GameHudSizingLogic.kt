package at.aau.serg.websocketbrokerdemo.ui.game

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Berechnet alle HUD-Groessen proportional zur Bildschirmbreite.
 *
 * Referenz ist das Samsung S21 Ultra mit ca. 384dp Breite -- dort
 * ergibt sich Scale-Factor 1.0. Min/Max-Clamping verhindert Extreme
 * auf sehr kleinen oder sehr grossen Screens.
 */

object GameHudSizingLogic
{
    // Referenzbreite: Samsung S21 Ultra (~384dp).
    private const val REF_WIDTH_DP = 360f

    // Clamping, damit es weder zu klein (unlesbar) noch zu gross
    // (laecherlich) wird.
    private const val MIN_SCALE = 0.75f
    private const val MAX_SCALE = 1.25f

    // Floor fuer Schriftgroessen, damit Texte lesbar bleiben.
    private const val MIN_TITLE_FONT_SP = 10f
    private const val MIN_SMALL_PRICE_FONT_SP = 9f
    private const val MIN_LARGE_PRICE_FONT_SP = 12f
    private const val MIN_GOLD_FONT_SP = 14f
    private const val MIN_LABEL_FONT_SP = 11f

    fun forWidth(widthDp: Float): GameHudSizing {
        val scale = (widthDp / REF_WIDTH_DP).coerceIn(MIN_SCALE, MAX_SCALE)
        return GameHudSizing(
            // Bottom-HUD
            bottomBarHeight = (100f * scale).dp,
            bottomBarSidePadding = 3.dp,
            bottomBarInnerPadding = (10f * scale).dp,
            bottomItemHorizontalPadding = (10f * scale).dp,
            bottomItemVerticalPadding = (6f * scale).dp,
            bottomIconSize = (50f * scale).dp,
            bottomCoinSpacing = (8f * scale).dp,
            bottomLabelTopPadding = (5f * scale).dp,
            bottomTitleFontSize = (12f * scale).coerceAtLeast(MIN_TITLE_FONT_SP).sp,
            bottomFarmPriceFontSize = (11f * scale).coerceAtLeast(MIN_SMALL_PRICE_FONT_SP).sp,
            bottomCoinPriceFontSize = (16f * scale).coerceAtLeast(MIN_LARGE_PRICE_FONT_SP).sp,

            // Top-HUD
            topRowTopPadding = (36f * scale).dp,
            topRowSidePadding = (10f * scale).dp,
            topBoxHorizontalPadding = (10f * scale).dp,
            topBoxVerticalPadding = (4f * scale).dp,
            topInnerSpacer = (6f * scale).dp,
            topGoldIconSize = (40f * scale).dp,
            topGoldFontSize = (24f * scale).coerceAtLeast(MIN_GOLD_FONT_SP).sp,
            topIncomeNumberFontSize = (22f * scale).coerceAtLeast(MIN_GOLD_FONT_SP).sp,
            topIncomeLabelFontSize = (16f * scale).coerceAtLeast(MIN_LABEL_FONT_SP).sp,
            topGiftIconSize = (40f * scale).dp,
            topMenuButtonSize = (52f * scale).dp,
            topMenuIconSize = (30f * scale).dp
        )
    }
}