package at.aau.serg.websocketbrokerdemo.data.serverside

data class Building(
    var player: String,
    var x: Int = 0,
    var y: Int = 0,
    var type: BuildingType
)
