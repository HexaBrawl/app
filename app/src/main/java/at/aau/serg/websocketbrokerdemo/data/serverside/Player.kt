package at.aau.serg.websocketbrokerdemo.data.serverside

/**
 * Spieler im Spiel.
 *
 * Die Felder spiegeln das Server-Modell wider. Gson deserialisiert
 * eingehende GameState-Updates direkt auf diese Klasse, daher muessen
 * Feldnamen und Defaults mit dem Backend uebereinstimmen.
 *
 *  - [gold]    Aktueller Gold-Bestand des Spielers.
 *  - [income]  Gold-Einkommen pro Runde (z. B. durch Farmen).
 */
data class Player(
    val name: String = "",
    val sessionId: String = "",
    val color: PlayerColor = PlayerColor.RED,
    val gold: Int = 0,
    val income: Int = 0
)
