package at.aau.serg.websocketbrokerdemo.data.serverside

//As per at.aau.hexabrawl.websocketserver.model
enum class ErrorCode {
    NOT_YOUR_TURN,
    INVALID_MOVE,
    GAME_FULL,
    GAME_NOT_STARTED
}

//As per at.aau.hexabrawl.websocketserver.model
data class ErorMessage(
    val errorCode: ErrorCode,
    val message: String
)
