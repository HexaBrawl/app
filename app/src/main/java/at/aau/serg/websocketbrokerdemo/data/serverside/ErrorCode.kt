package at.aau.serg.websocketbrokerdemo.data.serverside

/**
 * Fehlercodes, die der Server über das User-Queue-Topic
 * /user/queue/errors zurückschickt, wenn eine Client-Aktion
 * (Join, Move, End-Turn, Kauf, ...) abgelehnt wird.
 *
 * Muss exakt mit dem Server-Enum at.aau.hexabrawl.websocketserver.model.ErrorCode
 * uebereinstimmen, sonst kann Gson den eingehenden Fehler nicht deserialisieren.
 *
 *  - NOT_YOUR_TURN        Der Spieler hat versucht zu ziehen / die Runde
 *                         zu beenden, obwohl er nicht am Zug ist.
 *  - INVALID_MOVE         Der Move verletzt die Spielregeln (z.B. Ziel
 *                         ausserhalb der Reichweite, ungueltiger Konter).
 *  - GAME_FULL            Beitritt verweigert, weil der Raum bereits voll ist.
 *  - GAME_NOT_STARTED     Aktion verweigert, weil das Spiel noch nicht
 *                         im Status IN_PROGRESS ist.
 *  - INSUFFICIENT_GOLD    Kauf (Farm, Einheit) abgelehnt, weil das
 *                         Gold-Konto des Spielers nicht ausreicht.
 *  - COLOR_ALREADY_TAKEN  Beitritt verweigert, weil die gewuenschte
 *                         Spielerfarbe schon von jemandem anderen belegt ist.
 *  - ROOM_NOT_FOUND       Der angegebene roomId existiert nicht (mehr) in
 *                         der RoomRegistry. Wird auch beim Join mit
 *                         falschem JoinCode bzw. UUID gesendet.
 */
enum class ErrorCode {
    NOT_YOUR_TURN,
    INVALID_MOVE,
    GAME_FULL,
    GAME_NOT_STARTED,
    INSUFFICIENT_GOLD,
    COLOR_ALREADY_TAKEN,
    ROOM_NOT_FOUND
}
