package at.aau.serg.websocketbrokerdemo.ui.game

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import at.aau.serg.websocketbrokerdemo.ui.game.GameHudSizingLogic.forWidth

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
 * Nutzung: [BottomHud] und [tophud.TopHud] wrappen ihren Inhalt in
 * einen [androidx.compose.foundation.layout.BoxWithConstraints], rufen
 * [forWidth] und providen die Sizing ueber [GameHudSizingLogic] -- die
 * Components lesen daraus statt fixe dp/sp-Werte zu hardcoden.
 */
data class GameHudSizing(
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

        /** Default fuer Preview/Tests/Fallback (entspricht Referenzwert). */
        val Default: GameHudSizing = GameHudSizingLogic.forWidth(384f)
    }
}

/**
 * CompositionLocal fuer [GameHudSizing]. Die HUD-Container providen, die
 * Sub-Components lesen via `LocalHudSizing.current`.
 */
val LocalHudSizing = compositionLocalOf { GameHudSizing.Default }