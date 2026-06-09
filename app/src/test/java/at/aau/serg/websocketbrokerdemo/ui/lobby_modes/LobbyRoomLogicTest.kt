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
        val room = RoomDTO(
            roomId = "uuid-create",
            joinCode = "ROOM12",
            mode = GameMode.DUAL_VALLEY
        )
        val effects = LobbyRoomLogic.effectsForCreateResult(room)

        assertEquals(3, effects.size)
        assertTrue(effects.any { it is LobbyEffect.SetRoomId && it.roomId == "uuid-create" })
        assertTrue(effects.any { it is LobbyEffect.SetJoinCode && it.joinCode == "ROOM12" })
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
        val room = RoomDTO(
            roomId = "  ",
            joinCode = "ROOM12",
            mode = GameMode.DUAL_VALLEY
        )
        val effects = LobbyRoomLogic.effectsForCreateResult(room)

        assertEquals(1, effects.size)
        val effect = effects[0] as LobbyEffect.ShowError
        assertTrue(effect.message.contains("nicht erstellt"))
    }

    @Test
    fun `effectsForJoinByCodeResult returns success effects when room is valid`() {
        val room = RoomDTO(
            roomId = "uuid-join",
            joinCode = "ROOM45",
            mode = GameMode.DUAL_VALLEY
        )
        val effects = LobbyRoomLogic.effectsForJoinByCodeResult(room, "123456")

        assertEquals(4, effects.size)
        assertTrue(effects.any { it is LobbyEffect.SetRoomId && it.roomId == "uuid-join" })
        assertTrue(effects.any { it is LobbyEffect.SetJoinCode && it.joinCode == "ROOM45" })
        assertTrue(effects.any { it is LobbyEffect.CloseJoinDialog })
        assertTrue(effects.any { it is LobbyEffect.NavigateToWaiting })
    }

    @Test
    fun `effectsForJoinByCodeResult returns error including code when join fails`() {
        // Case 1: room is null
        val effectsNull = LobbyRoomLogic.effectsForJoinByCodeResult(null, "ABCDEF")
        assertEquals(1, effectsNull.size)
        assertTrue((effectsNull[0] as LobbyEffect.ShowError).message.contains("ABCDEF"))

        // Case 2: room is not null but roomId is blank
        val roomBlank = RoomDTO(
            roomId = "",
            joinCode = "JOIN42",
            mode = GameMode.DUAL_VALLEY
        )
        val effectsBlank = LobbyRoomLogic.effectsForJoinByCodeResult(roomBlank, "EFGHIJ")
        assertEquals(1, effectsBlank.size)
        assertTrue((effectsBlank[0] as LobbyEffect.ShowError).message.contains("EFGHIJ"))
    }

    @Test
    fun `effectsForJoinRandomResult returns success effects when room is valid`() {
        val room = RoomDTO(
            roomId = "uuid-random",
            joinCode = "RAND12",
            mode = GameMode.DUAL_VALLEY
        )
        val effects = LobbyRoomLogic.effectsForJoinRandomResult(room)

        assertEquals(3, effects.size)
        assertTrue(effects.any { it is LobbyEffect.SetRoomId && it.roomId == "uuid-random" })
        assertTrue(effects.any { it is LobbyEffect.SetJoinCode && it.joinCode == "RAND12" })
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
        // JoinByCodeLogic.isValid now requires exactly 6 chars
        val stateJoinable = LobbyState(code = "123456")
        val stateNotJoinable = LobbyState(code = "12345")

        assertTrue(LobbyRoomLogic.canAttemptJoin(stateJoinable))
        assertFalse(LobbyRoomLogic.canAttemptJoin(stateNotJoinable))
    }
}
