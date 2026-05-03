package at.aau.serg.websocketbrokerdemo.ui.mainmenu

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.myapplication.R

/**
 * Spielmodi.
 *
 * Jeder Modus weiß selbst, wie viele Spieler er hat, welcher Hintergrund
 * für seine Lobby gerendert wird und wie er in der UI heißt.
 *
 * Wird auch als Navigationsargument zwischen MainMenu und Lobby-Screens
 * übergeben (über die Route).
 */
enum class GameMode(
    val route: String,
    val playerCount: Int,
    @StringRes val nameRes: Int,
    @StringRes val taglineRes: Int,
    @DrawableRes val backgroundRes: Int
) {
    DUAL_VALLEY(
        route = "lobby_dual",
        playerCount = 2,
        nameRes = R.string.mode_dual_valley,
        taglineRes = R.string.mode_dual_valley_tagline,
        backgroundRes = R.drawable.bg_dual_valley
    ),
    TRIAD_OUTPOST(
        route = "lobby_triad",
        playerCount = 3,
        nameRes = R.string.mode_triad_outpost,
        taglineRes = R.string.mode_triad_outpost_tagline,
        backgroundRes = R.drawable.bg_triad_outpost
    ),
    BATTLEFIELD_PEAKS(
        route = "lobby_battlefield",
        playerCount = 4,
        nameRes = R.string.mode_battlefield_peaks,
        taglineRes = R.string.mode_battlefield_peaks_tagline,
        backgroundRes = R.drawable.bg_battlefield_peaks
    );

    companion object {
        fun fromRoute(route: String?): GameMode? =
            entries.firstOrNull { it.route == route }
    }
}
