package at.aau.serg.websocketbrokerdemo.ui.game

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Skalierungswerte fuer das HUD.
 *
 * Berechnet alle wichtigen Groessen (Icons, Schriften, Paddings)
 * proportional zur Bildschirmbreite. Referenz ist das Samsung S21 Ultra
 * mit ca. 384dp Breite -- die manuell justierten Werte (50dp Icons,
 * 12sp Title, 16sp Coin-Preis, 100dp Bar-Hoehe etc.) ergeben dort
 * einen Scale-Factor von 1.0.
 *
 * Auf kleineren Handies wird automatisch verkleinert, auf groesseren
 * (Tablets) leicht vergroessert. Min/Max-Clamping verhindert Extreme.
 *
 * Nutzung: BottomHud und TopHud wrappen ihren Inhalt in einen
 * [androidx.compose.foundation.layout.BoxWithConstraints], rufen
 * [forWidth] und providen die Sizing ueber LocalHudSizing -- die
 * Components lesen daraus statt fixe dp/sp-Werte zu hardcoden.
 */
data class HudSizing(
    // Bottom-HUD
    val bottomBarHeight: Dp,
    val bottomBarSidePadding: Dp,
    val bottomBarInnerPadding: Dp,
    val bottomItemHorizontalPadding: Dp,
    val bottomItemVerticalPadding: Dp,
    val bottomIconSize: Dp,
    val bottomCoinSpacing: Dp,
    val bottomLabelTopPadding: Dp,
    val bottomTitleFontSize: TextUnit,
    val bottomFarmPriceFontSize: TextUnit,
    val bottomCoinPriceFontSize: TextUnit,

    // Top-HUD
    val topRowTopPadding: Dp,
    val topRowSidePadding: Dp,
    val topBoxHorizontalPadding: Dp,
    val topBoxVerticalPadding: Dp,
    val topInnerSpacer: Dp,
    val topGoldIconSize: Dp,
    val topGoldFontSize: TextUnit,
    val topIncomeNumberFontSize: TextUnit,
    val topIncomeLabelFontSize: TextUnit,
    val topGiftIconSize: Dp,
    val topMenuButtonSize: Dp,
    val topMenuIconSize: Dp
) {
    companion object {

        // Referenzbreite: Samsung S21 Ultra (~384dp).
        private const val REF_WIDTH_DP = 384f

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

        /**
         * Berechnet die Sizing-Werte fuer eine gegebene Bildschirmbreite.
         */
        fun forWidth(width: Dp): HudSizing {
            val scale = (width.value / REF_WIDTH_DP).coerceIn(MIN_SCALE, MAX_SCALE)
            return HudSizing(
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

        /** Default fuer Preview/Tests/Fallback (entspricht Referenzwert). */
        val Default: HudSizing = forWidth(REF_WIDTH_DP.dp)
    }
}