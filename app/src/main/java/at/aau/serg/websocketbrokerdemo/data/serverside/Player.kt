package at.aau.serg.websocketbrokerdemo.data.serverside

/**
 * Spieler im Spiel.
 *
 * Felder spiegeln das Server-Modell wider; Gson deserialisiert eingehende
 * GameState-Updates direkt auf diese Klasse.
 *
 *  - name         Anzeigename des Spielers, vom User in der Lobby eingegeben.
 *                 Wird in allen Server-Requests als Identifikator benutzt
 *                 (z.B. Move.player, EndTurnRequest.playerName).
 *  - sessionId    STOMP-Session-ID, ueber die der Server diesen Spieler
 *                 fuer User-spezifische Nachrichten (Fehler) adressiert.
 *  - color        Spielerfarbe. Wird in der Lobby beim Join gewaehlt
 *                 und ist im Match einzigartig pro Raum.
 *  - gold         Aktueller Gold-Bestand. Wird fuer Kaeufe (Farm, Einheit)
 *                 verbraucht und am Rundenende durch income aufgestockt.
 *  - farms        Anzahl der vom Spieler bereits gekauften Farmen.
 *                 Wird vom Server zur Berechnung des naechsten Farm-Preises
 *                 benutzt: cost = FARM_BASE_COST + farms * FARM_COST_INCREMENT.
 *  - income       Gold-Einkommen pro Runde (steigt mit eroberten Feldern,
 *                 gebauten Farmen, ...).
 *  - hasUsedGift  Ob der Spieler die Schummel-Geschenk-Funktion in
 *                 diesem Match bereits einmal benutzt hat. Server-Truth,
 *                 damit der Geschenk-Button im HUD nicht client-side
 *                 umgangen werden kann.
 */
data class Player(
    val name: String = "",
    val sessionId: String = "",
    val color: PlayerColor = PlayerColor.RED,
    val gold: Int = 0,
    val farms: Int = 0,
    val income: Int = 0,
    val hasUsedGift: Boolean = false
)
