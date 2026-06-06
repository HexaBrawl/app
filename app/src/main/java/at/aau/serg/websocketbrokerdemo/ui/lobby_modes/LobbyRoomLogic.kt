package at.aau.serg.websocketbrokerdemo.ui.lobby_modes

import at.aau.serg.websocketbrokerdemo.data.serverside.RoomDTO

/**
 * Reine Logik fuer Lobby-Room-Aktionen.
 *
 * Berechnet aus einem Server-Ergebnis ([RoomDTO] oder null) die Liste
 * der State-Effekte, die das [LobbyViewModel] anwenden soll. Trifft
 * alle fachlichen Entscheidungen:
 *  - Was zaehlt als Erfolg (RoomId nicht leer + Antwort vorhanden)
 *  - Welche Fehlermeldung wird angezeigt
 *  - Ob der Dialog geschlossen wird
 *  - Ob navigiert wird
 *
 * Seiteneffekt-frei und damit ohne Coroutine- oder Compose-Runtime
 * testbar.
 */
object LobbyRoomLogic {

    /** Effekte fuer das Ergebnis eines createRoom-Aufrufs. */
    fun effectsForCreateResult(room: RoomDTO?): List<LobbyEffect> =
        if (room != null && room.roomId.isNotBlank()) {
            listOf(
                LobbyEffect.SetRoomId(room.roomId),
                LobbyEffect.NavigateToWaiting,
            )
        } else {
            listOf(LobbyEffect.ShowError("Raum konnte nicht erstellt werden"))
        }

    /** Effekte fuer das Ergebnis eines findByCode-Aufrufs. */
    fun effectsForJoinByCodeResult(room: RoomDTO?, code: String): List<LobbyEffect> =
        if (room != null && room.roomId.isNotBlank()) {
            listOf(
                LobbyEffect.SetRoomId(room.roomId),
                LobbyEffect.CloseJoinDialog,
                LobbyEffect.NavigateToWaiting,
            )
        } else {
            listOf(LobbyEffect.ShowError("Kein Raum mit Code '$code' gefunden"))
        }

    /** Effekte fuer das Ergebnis eines joinRandom-Aufrufs. */
    fun effectsForJoinRandomResult(room: RoomDTO?): List<LobbyEffect> =
        if (room != null && room.roomId.isNotBlank()) {
            listOf(
                LobbyEffect.SetRoomId(room.roomId),
                LobbyEffect.NavigateToWaiting,
            )
        } else {
            listOf(LobbyEffect.ShowError("Kein freier Raum gefunden"))
        }

    /**
     * Prueft, ob ein Join-Versuch ueberhaupt gestartet werden darf.
     * Wird vom ViewModel als Gate vor dem API-Call genutzt -- damit
     * landet auch diese Validierungs-Entscheidung nicht im VM.
     */
    fun canAttemptJoin(state: LobbyState): Boolean = state.canJoin
}