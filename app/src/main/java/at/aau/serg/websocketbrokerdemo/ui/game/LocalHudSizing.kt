package at.aau.serg.websocketbrokerdemo.ui.game

import androidx.compose.runtime.compositionLocalOf

/**
 * CompositionLocal fuer [HudSizing].
 *
 * BottomHud und TopHud providen die Sizing-Werte ueber dieses
 * CompositionLocal, damit die Sub-Components sie ohne Parameter-
 * Durchreichung lesen koennen.
 */
val LocalHudSizing = compositionLocalOf { HudSizing.Default }