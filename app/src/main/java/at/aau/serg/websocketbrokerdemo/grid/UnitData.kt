package at.aau.serg.websocketbrokerdemo.grid

/**
 * Eine Einheit auf einer bestimmten Zelle.
 *
 * Reduziert auf das, was der Renderer wirklich braucht: Position und
 * Spieler-Name. Wird vom GameScreen aus dem serverseitigen
 * `GameUnit`-Modell heraus gemappt (siehe GameMap.kt).
 */
data class UnitData(
    val x: Int,
    val y: Int,
    val player: String
)