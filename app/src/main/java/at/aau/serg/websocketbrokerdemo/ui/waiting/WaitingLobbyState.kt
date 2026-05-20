package at.aau.serg.websocketbrokerdemo.ui.waiting

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation.NavController
import at.aau.serg.websocketbrokerdemo.data.serverside.GameStatus
import at.aau.serg.websocketbrokerdemo.network.GameSession
import at.aau.serg.websocketbrokerdemo.ui.mainmenu.GameMode
import kotlinx.coroutines.delay

/**
 * Dev-only: when no second human player is available, auto-join a local "bot"
 * a few seconds after entering the lobby so the backend transitions to
 * IN_PROGRESS and the game can actually be played on one device.
 *
 * The bot is a real second STOMP join (so the server treats it as a normal
 * player); `GameScreen` watches for the bot's turn and sends moves for it.
 */
private const val DEV_AUTO_JOIN_BOT = true
private const val BOT_JOIN_DELAY_MS = 3000L

private val GENERAL_NAMES = listOf(
    "General Aldric", "General Borian", "General Cassia", "General Domitian",
    "General Eolyn", "General Faramond", "General Greta", "General Hadrik",
    "General Isolde", "General Joren", "General Käthe", "General Leofric",
    "General Mira", "General Nikolaus", "General Ortrun",
    "Lord-Marshal Quentin", "Hauptmann Reinhart", "Feldherr Sigmund",
    "Marschall Theodora", "General Ulric"
)

@Composable
fun rememberLobbySlots(mode: GameMode): SnapshotStateList<PlayerSlot> {
    return remember {
        mutableStateListOf<PlayerSlot>().apply {

            add(
                PlayerSlot(
                    id = 0,
                    status = SlotStatus.Player,
                    name = GENERAL_NAMES.random(),
                    color = PlayerColor.Red,
                    ready = false,
                    isLocal = true
                )
            )

            for (i in 1 until mode.playerCount) {
                add(PlayerSlot(id = i))
            }
        }
    }
}

@Composable
fun rememberAllReady(slots: List<PlayerSlot>): Boolean {
    return remember {
        derivedStateOf {
            slots.all {
                it.status != SlotStatus.Empty && it.ready
            }
        }
    }.value
}

@Composable
fun rememberCountdown(
    allReady: Boolean,
    navController: NavController
): Int {

    var countdown by remember { mutableIntStateOf(-1) }

    LaunchedEffect(allReady) {
        if (allReady) {

            countdown = 3

            while (countdown > 0) {
                delay(1000)
                countdown--
            }

            navController.navigate("game") {
                popUpTo("home") { inclusive = false }
            }

        } else {
            countdown = -1
        }
    }

    return countdown
}

/**
 * Sends `/app/join` for the local slot, mirrors the server's `GameState` into the
 * visible slot list, and triggers navigation to "game" as soon as the server
 * reports `IN_PROGRESS`. Replaces the old `AutoFillLobbySlots` bot mock.
 */
@Composable
fun SyncLobbyWithServer(
    session: GameSession,
    slots: SnapshotStateList<PlayerSlot>,
    navController: NavController
) {
    val localSlot = slots.firstOrNull { it.isLocal } ?: return
    val localName = localSlot.name

    LaunchedEffect(localName) {
        session.localPlayerName.value = localName
        session.endpoint.joinGame(localName)
    }

    // Dev shortcut: if after a few seconds the backend still only has us,
    // join a second "bot" player so the game can start on a single device.
    if (DEV_AUTO_JOIN_BOT) {
        LaunchedEffect(localName) {
            delay(BOT_JOIN_DELAY_MS)
            val state = session.gameState.value
            val alreadyHasPartner = state != null &&
                    state.players.any { it.name != localName }
            if (!alreadyHasPartner && session.botPlayerName.value == null) {
                val botName = "Bot-" + GENERAL_NAMES.random().substringAfter(' ')
                session.botPlayerName.value = botName
                session.endpoint.joinGame(botName)
            }
        }
    }

    val gameState by session.gameState
    LaunchedEffect(gameState) {
        val state = gameState ?: return@LaunchedEffect

        val remotePlayers = state.players.filter { it.name != localName }
        val takenColors = mutableSetOf(localSlot.color)

        slots.forEachIndexed { index, slot ->
            if (slot.isLocal) return@forEachIndexed

            val remoteIndex = index - 1
            val remote = remotePlayers.getOrNull(remoteIndex)
            slots[index] = if (remote == null) {
                slot.copy(status = SlotStatus.Empty, name = "", ready = false)
            } else {
                val color = PlayerColor.entries
                    .firstOrNull { it !in takenColors }
                    ?: PlayerColor.Blue
                takenColors += color
                slot.copy(
                    status = SlotStatus.Player,
                    name = remote.name,
                    color = color,
                    ready = true,
                    isLocal = false
                )
            }
        }

        if (state.status == GameStatus.IN_PROGRESS) {
            navController.navigate("game") {
                popUpTo("home") { inclusive = false }
            }
        }
    }
}
