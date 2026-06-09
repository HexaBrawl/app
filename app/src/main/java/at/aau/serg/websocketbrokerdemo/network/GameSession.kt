package at.aau.serg.websocketbrokerdemo.network

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import at.aau.serg.websocketbrokerdemo.data.serverside.ErrorMessage
import at.aau.serg.websocketbrokerdemo.data.serverside.GameState

/**
 * App-scoped holder for the live STOMP session.
 *
 * Lifecycle is owned by `MainActivity`. UI screens receive this object and
 * observe [gameState] / [lastError] instead of building their own connections.
 *
 *  - [activeRoomId]      Aktueller Raum, ueber den die Subscriptions laufen.
 *  - [gameState]         Letzter vom Server empfangener Spielzustand,
 *                        null vor der ersten Nachricht.
 *  - [lastError]         Letzte vom Server empfangene Fehlermeldung,
 *                        null wenn keine Fehler vorliegen.
 *  - [localPlayerName]   Name des lokalen Spielers, vom WaitingLobby-
 *                        Screen gesetzt sobald der User seinen Namen
 *                        bestaetigt hat.
 */
class GameSession(
    val endpoint: UnitMoveEndpoint,
    val activeRoomId: MutableState<String> = mutableStateOf(""),
    val gameState: MutableState<GameState?> = mutableStateOf(null),
    val lastError: MutableState<ErrorMessage?> = mutableStateOf(null),
    val localPlayerName: MutableState<String?> = mutableStateOf(null)
)