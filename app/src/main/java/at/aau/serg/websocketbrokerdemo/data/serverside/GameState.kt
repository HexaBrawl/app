package at.aau.serg.websocketbrokerdemo.data.serverside

/**
 * Spielzustand, wie ihn der Server ueber /topic/rooms/{roomId}/state
 * an alle Clients eines Raums broadcastet.
 *
 * Dieses DTO ist die "Wahrheit" - die UI-Schicht (ViewModels) leitet
 * ihren beobachtbaren State aus diesem Objekt ab und rendert die Map,
 * die Einheiten, das HUD usw. ausschliesslich auf Basis dieser Daten.
 *
 * Felder:
 *  - players       Alle Spieler im Raum (Name, Farbe, Gold, Einkommen, ...).
 *  - units         Alle Einheiten auf der Map (inkl. BASE der Spieler).
 *  - fields        Pro Hex-Feld der aktuelle Besitzer (oder null = neutral).
 *                  Wird vom Server beim Erobern von Feldern aktualisiert.
 *  - buildings     Errichtete Gebaeude (z.B. Farmen) inkl. Position und Besitzer.
 *  - currentTurn   Name des Spielers, der aktuell am Zug ist (oder null,
 *                  solange der Raum noch wartet bzw. das Spiel zu Ende ist).
 *  - status        Lebenszyklus des Matches: WAITING_FOR_PLAYERS, IN_PROGRESS,
 *                  FINISHED.
 *  - gameMode      Der gewaehlte Spielmodus (DUAL_VALLEY = 1v1, ...).
 *                  Bestimmt Map-Layout, Spawn-Punkte und Sieg-Bedingung.
 *  - winner        Name des Siegers, sobald status == FINISHED.
 *  - pendingGift   Falls != null, laeuft gerade ein "Schummel-Geschenk":
 *                  der Server akzeptiert solange keine regulaeren Moves
 *                  und antwortet mit einer Fehlermeldung.
 */
data class GameState(
    val players: MutableList<Player> = mutableListOf(),
    val units: MutableList<GameUnit> = mutableListOf(),
    val fields: MutableList<Field> = mutableListOf(),
    val buildings: MutableList<Building> = mutableListOf(),
    var currentTurn: String? = null,
    var status: GameStatus = GameStatus.WAITING_FOR_PLAYERS,
    var gameMode: GameMode = GameMode.DUAL_VALLEY,
    var winner: String? = null,
    var pendingGift: PendingGift? = null
)
