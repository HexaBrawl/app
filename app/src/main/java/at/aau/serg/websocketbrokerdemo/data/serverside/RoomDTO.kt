package at.aau.serg.websocketbrokerdemo.data.serverside

/**
 * Spiegelung des Server-DTOs für einen Spielraum (RoomDTO im Backend).
 *
 * Der Server unterscheidet zwischen zwei verschiedenen IDs:
 *  - roomId:   Eine technische UUID, die der Server intern verwendet.
 *              Wird in allen WebSocket-Endpoints als Pfad-Variable benutzt,
 *              z.B. /app/rooms/{roomId}/join, /move, /end-turn, ...
 *  - joinCode: Ein kurzer, menschenlesbarer 6-stelliger Code,
 *              den ein Spieler manuell in die Lobby eintippen kann,
 *              um einem Raum beizutreten.
 *
 * Die Liste der Spieler ist NICHT mehr Teil dieses DTOs - der Server
 * sendet die Spielerliste nur über den vollständigen GameState
 * (Topic /topic/rooms/{roomId}/state).
 */
data class RoomDTO(
    val roomId: String,
    val joinCode: String,
    val mode: GameMode,
    val maxPlayers: Int = 0,
    val currentPlayers: Int = 0
)
