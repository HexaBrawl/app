package at.aau.serg.websocketbrokerdemo.ui.game.tophud

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.aau.serg.websocketbrokerdemo.data.serverside.PendingGift
import at.aau.serg.websocketbrokerdemo.data.serverside.Player
import at.aau.serg.websocketbrokerdemo.ui.game.LocalHudSizing
import at.aau.serg.websocketbrokerdemo.ui.game.tophud.components.GoldDisplay
import at.aau.serg.websocketbrokerdemo.ui.game.tophud.components.HudMenuButton
import at.aau.serg.websocketbrokerdemo.ui.game.tophud.components.HudMenuPopup
import at.aau.serg.websocketbrokerdemo.ui.game.tophud.components.InGameSettingsPopup
import at.aau.serg.websocketbrokerdemo.ui.game.tophud.components.IncomeAndGiftDisplay
import at.aau.serg.websocketbrokerdemo.ui.game.tophud.components.InfoPopup
import at.aau.serg.websocketbrokerdemo.ui.game.tophud.components.StealPopup
import at.aau.serg.websocketbrokerdemo.ui.game.tophud.components.WaitingForOpponentOverlay

/**
 * Zeigt die obere Spielleiste an.
 *
 * Enthält (von links nach rechts): Goldanzeige, Einkommens- und
 * Geschenk-Anzeige, Menü-Button. Popups (Menü, Info, Einstellungen)
 * werden über [TopHudViewModel] gesteuert. Läuft ein offenes Geschenk-
 * Ereignis ([PendingGift]), wird je nach Rolle entweder ein
 * Warte-Overlay oder ein Diebstahl-Popup eingeblendet.
 */

@Composable
internal fun TopHudContent(
    players: List<Player>,
    localName: String?,
    pendingGift: PendingGift?,
    viewModel: TopHudViewModel,
    cheatViewModel: CheatGiftViewModel
) {
    val sizing = LocalHudSizing.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val cheatState by cheatViewModel.state.collectAsStateWithLifecycle()

    val gold = TopHudLogic.goldFor(players, localName)
    val income = TopHudLogic.incomeFor(players, localName)
    val giftEnabled = CheatGiftLogic.canUseGift(players, localName)

    LaunchedEffect(pendingGift) {
        if (pendingGift == null) {
            cheatViewModel.onPendingGiftCleared()
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = sizing.topRowTopPadding,
                start = sizing.topRowSidePadding,
                end = sizing.topRowSidePadding
            )
    ) {
        GoldDisplay(gold = gold)

        Spacer(Modifier.weight(1f))

        IncomeAndGiftDisplay(
            income = income,
            giftEnabled = giftEnabled,
            onGiftClick = { localName?.let { cheatViewModel.onGiftClick(it) } }
        )

        Spacer(Modifier.weight(1f))

        HudMenuButton(onClick = viewModel::openMenu)
    }

    when (state.popup) {
        HudPopup.None -> Unit
        HudPopup.Menu -> HudMenuPopup(
            onSettings = viewModel::showSettings,
            onInfo = viewModel::showInfo,
            onDismiss = viewModel::closePopup
        )
        HudPopup.Info -> InfoPopup(onDismiss = viewModel::closePopup)
        HudPopup.Settings -> InGameSettingsPopup(onDismiss = viewModel::closePopup)
    }

    if (pendingGift != null) {
        when {
            CheatGiftLogic.shouldShowWaitingOverlay(pendingGift, localName) -> {
                WaitingForOpponentOverlay(delta = pendingGift.delta)
            }
            CheatGiftLogic.shouldShowStealPopup(pendingGift, localName) &&
                    !cheatState.hasResponded -> {
                StealPopup(
                    ownerName = pendingGift.ownerName,
                    onAccept = { localName?.let { cheatViewModel.onStealAccept(it) } },
                    onDecline = { localName?.let { cheatViewModel.onStealDecline(it) } }
                )
            }
        }
    }
}