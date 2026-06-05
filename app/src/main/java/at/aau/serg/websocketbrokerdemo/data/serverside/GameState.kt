package at.aau.serg.websocketbrokerdemo.data.serverside

/**
 * Spielzustand wie ihn der Server liefert.
 *
 * [pendingGift] ist gesetzt waehrend ein Schummel-Geschenk laeuft.
 * Solange er nicht null ist, soll der Server keine Moves akzeptieren --
 * Move-Endpoints muessen dann mit einer entsprechenden Fehlermeldung
 * antworten.
 */
data class GameState(
    val players: MutableList<Player> = mutableListOf(),
    val units: MutableList<GameUnit> = mutableListOf(),
    val buildings: MutableList<Building> = mutableListOf(),
    var currentTurn: String? = null,
    var status: GameStatus = GameStatus.WAITING_FOR_PLAYERS,
    var pendingGift: PendingGift? = null
)
