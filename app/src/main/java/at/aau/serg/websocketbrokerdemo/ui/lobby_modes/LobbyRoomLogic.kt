package at.aau.serg.websocketbrokerdemo.ui.lobby_modes

import at.aau.serg.websocketbrokerdemo.data.serverside.GameMode
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
                LobbyEffect.SetJoinCode(room.joinCode),
                LobbyEffect.NavigateToWaiting,
            )
        } else {
            listOf(LobbyEffect.ShowError("Raum konnte nicht erstellt werden"))
        }

    /**
     * Effekte fuer das Ergebnis eines findByCode-Aufrufs.
     *
     * Schlaegt fehl, wenn kein Raum gefunden wurde ODER der gefundene Raum
     * einen anderen [GameMode] hat als der, aus dem der User beitreten will
     * ([expectedMode]) -- sonst landet er in einem fremden Spielmodus.
     */
    fun effectsForJoinByCodeResult(
        room: RoomDTO?,
        code: String,
        expectedMode: GameMode
    ): List<LobbyEffect> = when {
        room == null || room.roomId.isBlank() ->
            listOf(LobbyEffect.ShowError("Kein Raum mit Code '$code' gefunden"))
        room.mode != expectedMode ->
            listOf(
                LobbyEffect.ShowError(
                    "Dieser Code gehört zu einem ${room.mode}-Raum, du wolltest einen $expectedMode-Raum."
                )
            )
        else -> listOf(
            LobbyEffect.SetRoomId(room.roomId),
            LobbyEffect.SetJoinCode(room.joinCode),
            LobbyEffect.CloseJoinDialog,
            LobbyEffect.NavigateToWaiting,
        )
    }

    /**
     * Prueft, ob ein Join-Versuch ueberhaupt gestartet werden darf.
     * Wird vom ViewModel als Gate vor dem API-Call genutzt -- damit
     * landet auch diese Validierungs-Entscheidung nicht im VM.
     */
    fun canAttemptJoin(state: LobbyState): Boolean = state.canJoin
}