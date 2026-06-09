package at.aau.serg.websocketbrokerdemo.data.serverside

/**
 * Vom Server gesendete Fehlerstruktur. Wird ueber /user/queue/errors
 * an nur den ausloesenden Client zurueckgeschickt - andere Spieler im
 * Raum bekommen den Fehler nicht zu sehen.
 *
 * Muss mit dem Server-DTO at.aau.hexabrawl.websocketserver.model.ErrorMessage
 * uebereinstimmen, damit Gson den JSON-Body korrekt in dieses Datenobjekt
 * deserialisieren kann.
 */
data class ErrorMessage(
    val errorCode: ErrorCode,
    val message: String
)
