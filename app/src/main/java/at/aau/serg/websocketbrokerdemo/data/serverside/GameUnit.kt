package at.aau.serg.websocketbrokerdemo.data.serverside

/**
 * Eine einzelne Einheit auf der Spielkarte.
 *
 * Wird vom Server als Teil des GameState (Liste units) gesendet.
 * Muss mit dem Server-DTO at.aau.hexabrawl.websocketserver.model.GameUnit
 * uebereinstimmen, damit Gson das Update korrekt deserialisieren kann.
 *
 * Felder:
 *  - player            Name des Spielers, dem die Einheit gehoert
 *                      (matcht Player.name).
 *  - x, y              Aktuelle Position der Einheit auf der Hex-Map.
 *  - type              Einheitenklasse: ARCHER, INFANTRY, CAVALRY, SKELETON
 *                      oder BASE (die Heimatbasis des Spielers).
 *  - hasMovedThisTurn  true, wenn die Einheit in der aktuellen Runde
 *                      bereits gezogen ist. Wird vom Server beim
 *                      Rundenwechsel automatisch zurueckgesetzt und
 *                      verhindert, dass eine Einheit pro Runde mehrfach
 *                      bewegt wird. Die UI nutzt das Flag, um bereits
 *                      gezogene Einheiten visuell auszugrauen.
 */
data class GameUnit(
    var player: String,
    var x: Int = 0,
    var y: Int = 0,
    var type: UnitType,
    var hasMovedThisTurn: Boolean = false
)
