package at.aau.serg.websocketbrokerdemo.data.serverside

//As per at.aau.hexabrawl.websocketserver.model
data class Player (
    val name : String = "",
    val sessionId : String = "",
    val color: PlayerColor = PlayerColor.RED
)