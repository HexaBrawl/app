package at.aau.serg.websocketbrokerdemo.ui.game.tophud

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import at.aau.serg.websocketbrokerdemo.data.serverside.Player
import at.aau.serg.websocketbrokerdemo.ui.game.tophud.components.GoldDisplay
import at.aau.serg.websocketbrokerdemo.ui.game.tophud.components.HudMenuButton
import at.aau.serg.websocketbrokerdemo.ui.game.tophud.components.HudMenuPopup
import at.aau.serg.websocketbrokerdemo.ui.game.tophud.components.InGameSettingsPopup
import at.aau.serg.websocketbrokerdemo.ui.game.tophud.components.IncomeAndGiftDisplay
import at.aau.serg.websocketbrokerdemo.ui.game.tophud.components.InfoPopup

/**
 * Top-HUD des GameScreens.
 *
 * Drei freischwebende Boxen ueber der Karte:
 *  - Links:   Gold (Muenze + Zahl)
 *  - Mitte:   Einkommen pro Runde + Geschenk-Button
 *  - Rechts:  Menue-Button
 *
 * Keine Hintergrund-Leiste -- die Boxen sitzen frei auf der
 * Spielkarte. Das wirkt leichter und kostet weniger Bildschirmflaeche.
 */
@Composable
fun TopHud(
    players: List<Player>,
    localName: String?,
    modifier: Modifier = Modifier,
    viewModel: TopHudViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val gold = TopHudLogic.goldFor(players, localName)
    val income = TopHudLogic.incomeFor(players, localName)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 36.dp, start = 10.dp, end = 10.dp)
    ) {
        GoldDisplay(gold = gold)

        Spacer(Modifier.weight(1f))

        IncomeAndGiftDisplay(
            income = income,
            onGiftClick = { /* kommt in PR 2 */ }
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
}
