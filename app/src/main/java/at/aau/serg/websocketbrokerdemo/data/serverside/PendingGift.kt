package at.aau.serg.websocketbrokerdemo.data.serverside

/**
 * Repraesentiert ein gerade laufendes Schummel-Geschenk.
 *
 * Wenn ein Spieler 5x das Geschenk-Icon im HUD geklickt hat, wuerfelt
 * das Frontend ein Delta zwischen -10 und +10 Gold. Dieses Delta wird
 * sofort dem Spieler gutgeschrieben (oder abgezogen), und alle anderen
 * Spieler im Match koennen entscheiden ob sie es stehlen wollen.
 *
 * Der Server haelt diesen Zustand im [GameState] solange das Geschenk
 * "offen" ist:
 *  - [ownerName]   Wer das Geschenk geoeffnet hat
 *  - [delta]       Wieviel Gold die Aktion ergeben hat (-10..+10)
 *  - [pendingDecisions] Wieviele Gegner noch nicht entschieden haben
 *
 * Beim ersten "Ja" eines Stealers wird das Delta uebertragen (Owner
 * zurueck auf Start, Stealer bekommt +delta auch falls negativ) und
 * pendingGift wieder auf null gesetzt. Bei "Nein" wird pendingDecisions
 * runtergezaehlt; sobald 0 erreicht ist, ist das Geschenk beendet
 * ohne Klau.
 */
data class PendingGift(
    val ownerName: String,
    val delta: Int,
    val pendingDecisions: Int
)
