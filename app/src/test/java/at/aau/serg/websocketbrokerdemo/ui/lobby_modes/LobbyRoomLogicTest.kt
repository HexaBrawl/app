package at.aau.serg.websocketbrokerdemo.ui.lobby_modes

import at.aau.serg.websocketbrokerdemo.data.serverside.GameMode
import at.aau.serg.websocketbrokerdemo.data.serverside.RoomDTO
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LobbyRoomLogicTest {

    @Test
    fun `effectsForCreateResult returns success effects when room is valid`() {
        val room = RoomDTO(joinCode = "ROOM123", mode = GameMode.DUAL_VALLEY, players = emptyList())
        val effects = LobbyRoomLogic.effectsForCreateResult(room)

        assertEquals(2, effects.size)
        assertTrue(effects.any { it is LobbyEffect.SetRoomId && it.roomId == "ROOM123" })
        assertTrue(effects.any { it is LobbyEffect.NavigateToWaiting })
    }

    @Test
    fun `effectsForCreateResult returns error when room is null`() {
        val effects = LobbyRoomLogic.effectsForCreateResult(null)

        assertEquals(1, effects.size)
        val effect = effects[0] as LobbyEffect.ShowError
        assertTrue(effect.message.contains("nicht erstellt"))
    }

    @Test
    fun `effectsForCreateResult returns error when roomId is blank`() {
        val room = RoomDTO(joinCode = "  ", mode = GameMode.DUAL_VALLEY, players = emptyList())
        val effects = LobbyRoomLogic.effectsForCreateResult(room)

        assertEquals(1, effects.size)
        val effect = effects[0] as LobbyEffect.ShowError
        assertTrue(effect.message.contains("nicht erstellt"))
    }

    @Test
    fun `effectsForJoinByCodeResult returns success effects when room is valid`() {
        val room = RoomDTO(joinCode = "ROOM456", mode = GameMode.DUAL_VALLEY, players = emptyList())
        val effects = LobbyRoomLogic.effectsForJoinByCodeResult(room, "1234")

        assertEquals(3, effects.size)
        assertTrue(effects.any { it is LobbyEffect.SetRoomId && it.roomId == "ROOM456" })
        assertTrue(effects.any { it is LobbyEffect.CloseJoinDialog })
        assertTrue(effects.any { it is LobbyEffect.NavigateToWaiting })
    }

    @Test
    fun `effectsForJoinByCodeResult returns error including code when join fails`() {
        // Case 1: room is null
        val effectsNull = LobbyRoomLogic.effectsForJoinByCodeResult(null, "ABCD")
        assertEquals(1, effectsNull.size)
        assertTrue((effectsNull[0] as LobbyEffect.ShowError).message.contains("ABCD"))

        // Case 2: room is not null but roomId is blank
        val roomBlank = RoomDTO(joinCode = "", mode = GameMode.DUAL_VALLEY, players = emptyList())
        val effectsBlank = LobbyRoomLogic.effectsForJoinByCodeResult(roomBlank, "EFGH")
        assertEquals(1, effectsBlank.size)
        assertTrue((effectsBlank[0] as LobbyEffect.ShowError).message.contains("EFGH"))
    }

    @Test
    fun `effectsForJoinRandomResult returns success effects when room is valid`() {
        val room = RoomDTO(joinCode = "RAND123", mode = GameMode.DUAL_VALLEY, players = emptyList())
        val effects = LobbyRoomLogic.effectsForJoinRandomResult(room)

        assertEquals(2, effects.size)
        assertTrue(effects.any { it is LobbyEffect.SetRoomId && it.roomId == "RAND123" })
        assertTrue(effects.any { it is LobbyEffect.NavigateToWaiting })
    }

    @Test
    fun `effectsForJoinRandomResult returns error when room is null`() {
        val effects = LobbyRoomLogic.effectsForJoinRandomResult(null)

        assertEquals(1, effects.size)
        val effect = effects[0] as LobbyEffect.ShowError
        assertTrue(effect.message.contains("Kein freier Raum"))
    }

    @Test
    fun `canAttemptJoin returns state canJoin`() {
        // JoinByCodeLogic.isValid requires 4 to 8 chars
        val stateJoinable = LobbyState(code = "1234")
        val stateNotJoinable = LobbyState(code = "12")

        assertTrue(LobbyRoomLogic.canAttemptJoin(stateJoinable))
        assertFalse(LobbyRoomLogic.canAttemptJoin(stateNotJoinable))
    }
}
