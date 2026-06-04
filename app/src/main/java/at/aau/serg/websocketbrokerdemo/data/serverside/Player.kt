package at.aau.serg.websocketbrokerdemo.data.serverside

/**
 * Spieler im Spiel.
 *
 * Felder spiegeln das Server-Modell wider; Gson deserialisiert eingehende
 * GameState-Updates direkt auf diese Klasse.
 *
 *  - [gold]         Aktueller Gold-Bestand.
 *  - [income]       Gold-Einkommen pro Runde.
 *  - [hasUsedGift]  Ob der Spieler die Schummel-Geschenk-Funktion in
 *                   diesem Match bereits einmal benutzt hat. Server-Truth,
 *                   damit der Geschenk-Button im HUD nicht client-side
 *                   umgangen werden kann.
 */
data class Player(
    val name: String = "",
    val sessionId: String = "",
    val color: PlayerColor = PlayerColor.RED,
    val gold: Int = 0,
    val income: Int = 0,
    val hasUsedGift: Boolean = false
)
