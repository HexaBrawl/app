package at.aau.serg.websocketbrokerdemo.ui.navigation

/**
 * Pure Logik rund um die App-Navigation.
 *
 * Hier liegen die Entscheidungen, die ohne Compose-Runtime testbar sein
 * sollen -- etwa welcher Musik-Track zu welchem Screen gehört.
 *
 * Composables greifen über die statischen Helper auf diese Logik zu,
 * statt die Entscheidungen inline zu treffen. Dadurch bleibt der
 * Composable schlank und die Logik wird im Unit-Test abgedeckt.
 */
object NavigationLogic {

    /**
     * Bestimmt den passenden Musik-Track für eine gegebene Route.
     *
     * Heuristik:
     *  - Alle Waiting*-Screens spielen den Tournament-Track
     *  - Game spielt den Battle-Track
     *  - Alle übrigen Routen (Home, Settings, MainMenu, Lobby*) spielen
     *    den Menü-Track
     *  - Eine unbekannte oder null-Route fällt auf den Menü-Track zurück
     *    (defensives Default, verhindert dass die Musik stehen bleibt)
     *
     * @param route die aktuelle Backstack-Route, kann null sein wenn
     *              der NavController noch keinen aktiven Eintrag hat
     *              (z. B. direkt nach App-Start vor der ersten
     *              Komposition).
     */
    fun trackForRoute(route: String?): MusicTrack {
        val screen = Screen.fromRoute(route) ?: return MusicTrack.Menu
        return when (screen) {
            Screen.WaitingDual,
            Screen.WaitingTriad,
            Screen.WaitingBattlefield -> MusicTrack.Tournament

            Screen.Game -> MusicTrack.Battle

            Screen.Home,
            Screen.Settings,
            Screen.MainMenu,
            Screen.LobbyDual,
            Screen.LobbyTriad,
            Screen.LobbyBattlefield,
            Screen.EndWin,
            Screen.EndLoss -> MusicTrack.Menu
        }
    }
}
