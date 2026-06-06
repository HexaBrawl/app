package at.aau.serg.websocketbrokerdemo.ui.game.tophud

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import at.aau.serg.websocketbrokerdemo.data.serverside.PendingGift
import at.aau.serg.websocketbrokerdemo.data.serverside.Player
import at.aau.serg.websocketbrokerdemo.network.GameSession
import at.aau.serg.websocketbrokerdemo.ui.game.GameHudSizing
import at.aau.serg.websocketbrokerdemo.ui.game.LocalHudSizing
import at.aau.serg.websocketbrokerdemo.ui.game.GameHudSizingLogic

/**
 * Top-HUD des GameScreens.
 *
 * Drei freischwebende Boxen am oberen Bildschirmrand plus die Popups
 * fuer Menue/Info/Settings/Steal/Waiting.
 *
 * Skaliert sich automatisch mit der Bildschirmbreite ueber [HudSizing] +
 * [GameHudSizingLogic].
 */

@Composable
fun TopHud(
    players: List<Player>,
    localName: String?,
    pendingGift: PendingGift?,
    session: GameSession,
    modifier: Modifier = Modifier,
    viewModel: TopHudViewModel = viewModel(),
    cheatViewModel: CheatGiftViewModel = viewModel(
        factory = viewModelFactory {
            initializer { CheatGiftViewModel(session) }
        }
    )
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val sizing = remember(maxWidth) { GameHudSizingLogic.forWidth(maxWidth.value) }

        CompositionLocalProvider(LocalHudSizing provides sizing) {
            TopHudContent(
                players = players,
                localName = localName,
                pendingGift = pendingGift,
                viewModel = viewModel,
                cheatViewModel = cheatViewModel
            )
        }
    }
}