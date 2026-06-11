package at.aau.serg.websocketbrokerdemo.ui.mainmenu

import at.aau.serg.websocketbrokerdemo.data.serverside.GameMode as ServerGameMode

/**
 * Uebersetzt den Server-GameMode in den UI-GameMode.
 *
 * Beide Enums tragen die gleichen Namen, aber leben in unterschied-
 * lichen Schichten: der Server-Enum kommt aus dem Daten-Layer
 * (deserialisiert aus dem GameState-JSON), der UI-Enum traegt
 * zusaetzlich Drawable- und String-Resources.
 *
 * Die `when`-Variante ist exhaustive -- wenn der Server spaeter einen
 * vierten Modus bekommt, fordert der Compiler hier eine Anpassung an.
 */
fun ServerGameMode.toUiMode(): GameMode = when (this) {
    ServerGameMode.DUAL_VALLEY -> GameMode.DUAL_VALLEY
    ServerGameMode.TRIAD_OUTPOST -> GameMode.TRIAD_OUTPOST
    ServerGameMode.BATTLEFIELD_PEAKS -> GameMode.BATTLEFIELD_PEAKS
}
