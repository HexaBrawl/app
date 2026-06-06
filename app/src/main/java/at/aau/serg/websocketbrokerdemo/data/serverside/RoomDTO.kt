package at.aau.serg.websocketbrokerdemo.data.serverside

/**
 * Spiegelung des Server-DTOs für einen Spielraum.
 *
 * @property joinCode Der 6-stellige Beitritts-Code.
 * @property mode Der Spielmodus.
 * @property players Liste der beigetretenen Spieler.
 */
data class RoomDTO(
    val joinCode: String,
    val mode: GameMode,
    val players: List<Player>
) {
    /** Alias fuer LobbyRoomLogic, die roomId erwartet. */
    val roomId: String get() = joinCode
}
