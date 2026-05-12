package at.aau.serg.websocketbrokerdemo.ui.navigation

enum class MusicTrack { Menu, Tournament, Battle }

object NavigationLogic
{
    fun trackForRoute(route: String?): MusicTrack {
        if (route == null) return MusicTrack.Menu

        if (route.startsWith("waiting_")) {
            return MusicTrack.Tournament
        }

        if (route == "game") {
            return MusicTrack.Battle
        }

        return MusicTrack.Menu
    }

}