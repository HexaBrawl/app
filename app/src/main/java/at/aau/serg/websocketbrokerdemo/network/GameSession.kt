package at.aau.serg.websocketbrokerdemo.network

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import at.aau.serg.websocketbrokerdemo.data.serverside.ErrorMessage
import at.aau.serg.websocketbrokerdemo.data.serverside.GameState

/**
 * App-scoped holder for the live STOMP session.
 *
 * Lifecycle is owned by `MainActivity`. UI screens receive this object and observe
 * `gameState` / `lastError` instead of building their own connections.
 *
 * `botPlayerName` is set when the dev-bot has joined the backend; the `GameScreen`
 * watches it and auto-plays the bot's turn.
 *
 * `gameStateReceivedCount` / `errorReceivedCount` are diagnostic counters - they
 * let the UI show whether the server is actually answering after a move.
 */
class GameSession(
    val endpoint: UnitMoveEndpoint,
    val gameState: MutableState<GameState?> = mutableStateOf(null),
    val lastError: MutableState<ErrorMessage?> = mutableStateOf(null),
    val localPlayerName: MutableState<String?> = mutableStateOf(null),
    val botPlayerName: MutableState<String?> = mutableStateOf(null),
    val gameStateReceivedCount: androidx.compose.runtime.MutableIntState = mutableIntStateOf(0),
    val errorReceivedCount: androidx.compose.runtime.MutableIntState = mutableIntStateOf(0)
)
