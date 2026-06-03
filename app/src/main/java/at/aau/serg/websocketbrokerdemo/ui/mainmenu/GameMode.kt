package at.aau.serg.websocketbrokerdemo.ui.mainmenu

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.myapplication.R

/**
 * Spielmodi.
 *
 * Jeder Modus weiss selbst, wie viele Spieler er hat, welcher
 * Hintergrund fuer seine Lobby gerendert wird und wie er in der UI
 * heisst.
 *
 * Die Verknuepfung zu Compose Navigation lebt in [MainMenuLogic.screenForMode]
 * bzw. [at.aau.serg.websocketbrokerdemo.ui.lobby_modes.LobbyLogic.toWaitingScreen] --
 * der Enum selbst kennt keine Routen-Strings mehr.
 */
enum class GameMode(
    val playerCount: Int,
    @param:StringRes val nameRes: Int,
    @param:StringRes val taglineRes: Int,
    @param:DrawableRes val backgroundRes: Int
) {
    DUAL_VALLEY(
        playerCount = 2,
        nameRes = R.string.mode_dual_valley,
        taglineRes = R.string.mode_dual_valley_tagline,
        backgroundRes = R.drawable.bg_dual_valley
    ),
    TRIAD_OUTPOST(
        playerCount = 3,
        nameRes = R.string.mode_triad_outpost,
        taglineRes = R.string.mode_triad_outpost_tagline,
        backgroundRes = R.drawable.bg_triad_outpost
    ),
    BATTLEFIELD_PEAKS(
        playerCount = 4,
        nameRes = R.string.mode_battlefield_peaks,
        taglineRes = R.string.mode_battlefield_peaks_tagline,
        backgroundRes = R.drawable.bg_battlefield_peaks
    )
}
