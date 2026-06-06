package at.aau.serg.websocketbrokerdemo.ui.game.tophud

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import at.aau.serg.websocketbrokerdemo.data.serverside.PendingGift
import at.aau.serg.websocketbrokerdemo.data.serverside.Player
import at.aau.serg.websocketbrokerdemo.network.GameSession
import at.aau.serg.websocketbrokerdemo.ui.game.HudSizing
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
 * Top-HUD des GameScreens.
 *
 * Drei freischwebende Boxen am oberen Bildschirmrand plus die Popups
 * fuer Menue/Info/Settings/Steal/Waiting.
 *
 * Skaliert sich automatisch mit der Bildschirmbreite ueber [HudSizing] +
 * [LocalHudSizing]. Sub-Components lesen die Werte aus dem
 * CompositionLocal -- keine durchgereichten Sizing-Parameter noetig.
 *
 * Schummel-Geschenk-Flow:
 *  - Klick aufs Geschenk-Icon laeuft ueber [CheatGiftViewModel]
 *  - Nach 5 Klicks wird das Delta gewuerfelt und an den Server geschickt
 *  - Server setzt pendingGift -> die UI reagiert:
 *      * Owner sieht das WaitingForOpponentOverlay
 *      * Andere Spieler sehen das StealPopup
 *  - Sobald pendingGift weg ist, wird der lokale "schon entschieden"-
 *    State zurueckgesetzt fuer ein eventuelles naechstes Geschenk
 *    eines anderen Spielers.
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
        val sizing = remember(maxWidth) { HudSizing.forWidth(maxWidth) }

        CompositionLocalProvider(LocalHudSizing provides sizing) {
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
    }
}