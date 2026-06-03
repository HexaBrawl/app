package at.aau.serg.websocketbrokerdemo.ui.game

/**
 * Toggle fuer die Entwickler-Debug-Anzeigen im GameScreen.
 *
 * Wenn true: am unteren Bildschirmrand wird ein Panel mit
 * Status/Turn/Last-Tap/Last-Move/Units eingeblendet, das beim
 * Debugging der Server-Kommunikation hilft.
 *
 * Wenn false: die Debug-Anzeigen sind komplett versteckt, der
 * GameScreen rendert nur das Spielbrett.
 *
 * Wird als private const val gehalten (kein BuildConfig.DEBUG), damit
 * der Wert sowohl in Debug- als auch Release-Builds gesetzt werden
 * kann, ohne Build-Varianten anfassen zu muessen.
 */
internal const val DEBUG_HUD = true
