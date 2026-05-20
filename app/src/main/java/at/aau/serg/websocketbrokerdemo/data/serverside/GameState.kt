package at.aau.serg.websocketbrokerdemo.data.serverside

//As per at.aau.hexabrawl.websocketserver.model
data class GameState(
    val players: MutableList<Player> = mutableListOf(),
    val units: MutableList<GameUnit> = mutableListOf(),
    var currentTurn: String? = null,
    var status: GameStatus = GameStatus.WAITING_FOR_PLAYERS
)