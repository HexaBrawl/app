package at.aau.serg.websocketbrokerdemo.ui.lobby_modes

/**
 * UI-State der Modus-Lobby.
 *
 * Vom [LobbyViewModel] ueber einen StateFlow exponiert. Der
 * [LobbyScreen] liest ausschliesslich diesen State und ruft
 * Handler-Methoden des ViewModels auf -- keine `remember`-State-
 * Verwaltung im Composable.
 *
 *  - [showJoinDialog] Ob der "Mit Code beitreten"-Dialog gerade sichtbar ist
 *  - [code]           Aktueller Inhalt des Code-Eingabefelds (normalisiert)
 */
data class LobbyState(
    val showJoinDialog: Boolean = false,
    val code: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
) {
    /** True wenn der aktuelle Code gueltig ist (4-8 Zeichen) und nicht geladen wird. */
    val canJoin: Boolean
        get() = !isLoading && JoinByCodeLogic.isValid(code)
}
