package at.aau.serg.websocketbrokerdemo.data.serverside

/**
 * Server-seitige Entsprechung der Spielmodi.
 *
 * @property maxPlayers Maximale Anzahl an Spielern für diesen Modus.
 */
enum class GameMode(val maxPlayers: Int) {
    DUAL_VALLEY(2),
    TRIAD_OUTPOST(3),
    BATTLEFIELD_PEAKS(4)
}
