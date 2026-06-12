package at.aau.serg.websocketbrokerdemo.data.serverside

/**
 * Lebenszyklus eines Match-Raums, wie ihn der Server im [GameState]
 * broadcastet.
 *
 * Werte entsprechen dem Server-Enum
 * `at.aau.hexabrawl.websocketserver.model.GameStatus`, damit Gson sie
 * ohne Mapping deserialisiert.
 */
enum class GameStatus {
    /** Raum besteht, der Server wartet noch auf weitere Spieler. */
    WAITING_FOR_PLAYERS,

    /** Spiel laeuft, Moves werden akzeptiert. */
    IN_PROGRESS,

    /** Spiel beendet, [GameState.winner] ist gesetzt. */
    FINISHED
}