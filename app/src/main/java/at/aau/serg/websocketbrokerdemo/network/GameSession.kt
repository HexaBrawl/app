package at.aau.serg.websocketbrokerdemo.network

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import at.aau.serg.websocketbrokerdemo.data.SessionRepository
import at.aau.serg.websocketbrokerdemo.data.serverside.ErrorMessage
import at.aau.serg.websocketbrokerdemo.data.serverside.GameState

/**
 * App-scoped holder for the live STOMP session.
 *
 * Lifecycle is owned by `MainActivity`. UI screens receive this object and
 * observe [gameState] / [lastError] instead of building their own connections.
 *
 *  - [activeRoomId]      Aktueller Raum (UUID), ueber den die Subscriptions
 *                        und alle STOMP-Pfade laufen
 *                        (/app/rooms/{roomId}/join, /move, /end-turn, ...).
 *  - [activeJoinCode]    Menschenlesbarer 6-Zeichen-Code des aktuellen Raums.
 *                        Wird ausschliesslich fuer Anzeige + Clipboard in der
 *                        Wartelobby verwendet -- NICHT fuer STOMP-Pfade.
 *  - [gameState]         Letzter vom Server empfangener Spielzustand,
 *                        null vor der ersten Nachricht.
 *  - [lastError]         Letzte vom Server empfangene Fehlermeldung,
 *                        null wenn keine Fehler vorliegen.
 *  - [localPlayerName]   Name des lokalen Spielers, vom WaitingLobby-
 *                        Screen gesetzt sobald der User seinen Namen
 *                        bestaetigt hat.
 *  - [sessionRepository] In-Memory-Spiegel von roomId / joinCode /
 *                        playerName fuer Reconnect- und Leave-Calls.
 *                        Die einzelnen MutableState-Felder oben bleiben
 *                        Source-of-Truth fuer die UI; das Repository
 *                        wird parallel beschrieben, damit Endpoints
 *                        ohne UI-Bezug (Activity-Lifecycle, Auto-
 *                        Reconnect-Callback) sauberen Zugriff haben.
 */
class GameSession(
    val endpoint: UnitMoveEndpoint,
    val activeRoomId: MutableState<String> = mutableStateOf(""),
    val activeJoinCode: MutableState<String> = mutableStateOf(""),
    val gameState: MutableState<GameState?> = mutableStateOf(null),
    val lastError: MutableState<ErrorMessage?> = mutableStateOf(null),
    val localPlayerName: MutableState<String?> = mutableStateOf(null),
    val sessionRepository: SessionRepository = SessionRepository()
)
