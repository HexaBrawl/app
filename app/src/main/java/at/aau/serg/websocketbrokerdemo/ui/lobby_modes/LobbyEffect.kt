package at.aau.serg.websocketbrokerdemo.ui.lobby_modes

/**
 * Beschreibt eine einzelne State-Mutation, die das [LobbyViewModel]
 * nach einer Lobby-Room-Aktion durchfuehren soll.
 *
 * Wird von [LobbyRoomLogic] aus einem Server-Ergebnis berechnet -- das
 * ViewModel selbst trifft keine fachlichen Entscheidungen mehr, sondern
 * dispatched nur noch jeden Effect 1:1 auf einen State-Setter. Damit
 * landet die gesamte Logik in einem testbaren, seiteneffekt-freien
 * Object.
 *
 * Pattern: Unidirectional Data Flow / Effect-Pattern, vergleichbar mit
 * Elm-Cmd oder Spotify-Mobius.
 */
sealed interface LobbyEffect {
    /** Aktive Room-Id der GameSession setzen. */
    data class SetRoomId(val roomId: String) : LobbyEffect

    /** Eine Fehlermeldung im UI anzeigen. */
    data class ShowError(val message: String) : LobbyEffect

    /** Den "Beitreten via Code"-Dialog schliessen. */
    data object CloseJoinDialog : LobbyEffect

    /** Zur WaitingLobby navigieren (loest den onSuccess-Callback aus). */
    data object NavigateToWaiting : LobbyEffect
}
