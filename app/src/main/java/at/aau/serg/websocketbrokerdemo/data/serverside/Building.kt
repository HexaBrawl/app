package at.aau.serg.websocketbrokerdemo.data.serverside

/**
 * Ein vom Spieler errichtetes Gebaeude auf der Hex-Map.
 *
 * Wird vom Server als Teil des [GameState] (Liste `buildings`)
 * mitgeschickt. Muss mit dem Server-DTO
 * `at.aau.hexabrawl.websocketserver.model.Building` uebereinstimmen,
 * damit Gson die Updates korrekt deserialisieren kann.
 *
 *  - [player] Name des Spielers, dem das Gebaeude gehoert (matcht
 *             [Player.name]); bestimmt die Farbe beim Rendering.
 *  - [x], [y] Position auf der Hex-Map (offset-Koordinaten).
 *  - [type]   Gebaeudeklasse, siehe [BuildingType].
 */
data class Building(
    var player: String,
    var x: Int = 0,
    var y: Int = 0,
    var type: BuildingType
)
