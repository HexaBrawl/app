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
 *                 verbraucht und am Rundenende durch income aufgestockt
 *                 sowie um upkeep verringert.
 *  - farms        Anzahl der vom Spieler bereits gekauften Farmen.
 *                 Wird vom Server zur Berechnung des naechsten Farm-Preises
 *                 benutzt: cost = FARM_BASE_COST + farms * FARM_COST_INCREMENT.
 *  - income       Brutto-Gold-Einkommen pro Runde. Server-Truth, gerechnet als
 *                 farms * FARM_INCOME_PER_ROUND + ownedFields * FIELD_INCOME_PER_ROUND
 *                 (Skelett-Felder zaehlen nicht).
 *  - upkeep       Truppenunterhalt pro Runde. Server-Truth, gerechnet als
 *                 (0 until unitCount).sumOf { 3 + it } -- die erste Kampf-
 *                 Einheit kostet 3 Gold, jede weitere +1. Reicht das Gold
 *                 am Rundenende nicht, werden alle Truppen zu Skeletten.
 *  - hasUsedGift  Ob der Spieler die Schummel-Geschenk-Funktion in
 *                 diesem Match bereits einmal benutzt hat. Server-Truth,
 *                 damit der Geschenk-Button im HUD nicht client-side
 *                 umgangen werden kann.
 *  - connected    Ob die WebSocket-Verbindung des Spielers gerade
 *                 lebt. Default true (Server sendet auch im Reconnect-
 *                 Pfad nichts auf alten Clients, dann gilt true). Wird
 *                 vom Server auf false gesetzt sobald die Heartbeats
 *                 ausbleiben; geht nach erfolgreichem /reconnect
 *                 zurueck auf true oder verschwindet (Hard-Delete nach
 *                 30s Grace-Period). UI zeigt waehrenddessen den
 *                 DisconnectedPlayerOverlay.
 */
data class Player(
    val name: String = "",
    val sessionId: String = "",
    val color: PlayerColor = PlayerColor.RED,
    val gold: Int = 0,
    val farms: Int = 0,
    val income: Int = 0,
    val upkeep: Int = 0,
    val hasUsedGift: Boolean = false,
    val connected: Boolean = true
)
