package at.aau.serg.websocketbrokerdemo.ui.waiting

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation.NavController
import at.aau.serg.websocketbrokerdemo.ui.mainmenu.GameMode
import kotlinx.coroutines.delay

private const val DEV_AUTO_FILL_SLOTS = true

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

@Composable
fun AutoFillLobbySlots(slots: SnapshotStateList<PlayerSlot>) {

    if (!DEV_AUTO_FILL_SLOTS) return

    LaunchedEffect(Unit) {

        delay(5000)

        slots.forEachIndexed { i, slot ->

            if (slot.status == SlotStatus.Empty) {

                val taken = slots
                    .filter {
                        it.id != slot.id &&
                                it.status != SlotStatus.Empty
                    }
                    .map { it.color }
                    .toSet()

                val freeColor =
                    PlayerColor.entries.firstOrNull { it !in taken }
                        ?: PlayerColor.Red

                slots[i] = slot.copy(
                    status = SlotStatus.Bot,
                    name = "Bot-${GENERAL_NAMES.random().substringAfter(' ')}",
                    color = freeColor,
                    ready = true
                )
            }
        }
    }
}