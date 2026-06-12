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
 *
 * Loading- und Error-State leben bewusst als eigene StateFlows
 * (`isLoading`, `lastError`) am ViewModel, weil sie an Coroutine-Aufrufe
 * gekoppelt sind und gegenueber UI-Inputs eine andere Lebensdauer haben.
 */
data class LobbyState(
    val showJoinDialog: Boolean = false,
    val code: String = ""
) {
    /**
     * True wenn der aktuelle Code-Inhalt akzeptabel ist (genau
     * [JoinByCodeLogic.MIN_LENGTH] Zeichen). Der Loading-Guard kommt
     * zusaetzlich im Screen/ViewModel dazu.
     */
    val canJoin: Boolean
        get() = JoinByCodeLogic.isValid(code)
}
