package at.aau.serg.websocketbrokerdemo.data.serverside

/**
 * Bewegungs- bzw. Angriffsbefehl einer Einheit.
 *
 * Wird vom Client ueber STOMP an den Server geschickt, sobald der
 * Spieler eine eigene Einheit selektiert und eine Ziel-Zelle antippt.
 * Der Server validiert (Reichweite, Besitzer, Spielstatus) und
 * antwortet entweder mit einem aktualisierten [GameState] oder einer
 * [ErrorMessage].
 *
 *  - [player]         Name des Spielers, der den Zug initiiert.
 *  - [type]           Einheitenklasse — der Server prueft damit, ob
 *                     die getroffene Einheit zum gewaehlten Typ passt.
 *  - [fromX], [fromY] Ausgangsposition der Einheit.
 *  - [toX], [toY]     Ziel-Zelle.
 *
 * Muss mit dem Server-DTO
 * `at.aau.hexabrawl.websocketserver.model.Move` uebereinstimmen.
 */
data class Move(
    var player: String = "",
    var type: UnitType = UnitType.INFANTRY,
    var fromX: Int = 0,
    var fromY: Int = 0,
    var toX: Int = 0,
    var toY: Int = 0
)