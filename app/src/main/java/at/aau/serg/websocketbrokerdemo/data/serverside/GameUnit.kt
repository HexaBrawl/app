package at.aau.serg.websocketbrokerdemo.data.serverside

//As per at.aau.hexabrawl.websocketserver.model
data class GameUnit(
    var player: String,
    var x: Int = 0,
    var y: Int = 0,
    var type: UnitType
)