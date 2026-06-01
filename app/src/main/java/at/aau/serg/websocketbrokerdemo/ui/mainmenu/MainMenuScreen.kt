package at.aau.serg.websocketbrokerdemo.ui.mainmenu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import at.aau.serg.websocketbrokerdemo.ui.mainmenu.components.HotspotMarker
import at.aau.serg.websocketbrokerdemo.ui.mainmenu.components.InfoDialog
import at.aau.serg.websocketbrokerdemo.ui.mainmenu.components.ModeConfirmDialog
import at.aau.serg.websocketbrokerdemo.ui.mainmenu.components.RoundCoinIconButton
import at.aau.serg.websocketbrokerdemo.ui.theme.WoodDark
import at.aau.serg.websocketbrokerdemo.ui.theme.WoodMedium
import com.example.myapplication.R

/**
 * MainMenuScreen — der "Kriegstisch".
 *
 * Reine UI-Schicht. Verantwortlich nur für:
 *  - Hintergrund + Vignette rendern
 *  - Hotspots positionieren (BoxWithConstraints + HotspotCalculator)
 *  - Top-Bar-Buttons (Zurück / Info)
 *  - Dialoge anzeigen, wenn der State sagt "ja"
 *
 * Sämtliche State-Verwaltung läuft im [MainMenuViewModel], sämtliche
 * Navigation in [MainMenuLogic]. Einzelne Composable-Bausteine
 * (Hotspots, Dialoge, Buttons) liegen in `components/`.
 */
@Composable
fun MainMenuScreen(
    navController: NavController,
    viewModel: MainMenuViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {

        // Holz-Hintergrund (sichtbar an den Rändern, falls Bild kleiner ist)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(WoodMedium, WoodDark),
                        radius = 1500f
                    )
                )
        )

        // Karte + Hotspots
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val w = maxWidth
            val h = maxHeight

            Image(
                painter = painterResource(id = R.drawable.bg_mainmenu),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )

            HotspotMarker(
                xPct = 0.49f, yPct = 0.150f, parentW = w, parentH = h,
                onTap = { viewModel.onHotspotTapped(GameMode.DUAL_VALLEY) }
            )
            HotspotMarker(
                xPct = 0.495f, yPct = 0.435f, parentW = w, parentH = h,
                onTap = { viewModel.onHotspotTapped(GameMode.TRIAD_OUTPOST) }
            )
            HotspotMarker(
                xPct = 0.50f, yPct = 0.700f, parentW = w, parentH = h,
                onTap = { viewModel.onHotspotTapped(GameMode.BATTLEFIELD_PEAKS) }
            )
        }

        // Zurück-Button
        RoundCoinIconButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.settings_back),
            onClick = { MainMenuLogic.navigateBack(navController) },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        )

        // Info-Button
        RoundCoinIconButton(
            icon = Icons.Filled.Info,
            contentDescription = stringResource(R.string.menu_info),
            onClick = { viewModel.onInfoClicked() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        )
    }

    // Bestätigungs-Popup
    state.pendingMode?.let { mode ->
        ModeConfirmDialog(
            mode = mode,
            onConfirm = {
                viewModel.onConfirmPendingMode()
                MainMenuLogic.navigateToLobby(navController, mode)
            },
            onDismiss = { viewModel.onDismissPendingMode() }
        )
    }

    // Info-Dialog
    if (state.showInfo) {
        InfoDialog(onDismiss = { viewModel.onDismissInfo() })
    }
}
