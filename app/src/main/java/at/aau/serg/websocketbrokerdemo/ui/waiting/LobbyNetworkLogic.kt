package at.aau.serg.websocketbrokerdemo.ui.waiting

import at.aau.serg.websocketbrokerdemo.data.serverside.ErrorCode
import at.aau.serg.websocketbrokerdemo.data.serverside.GameStatus
import at.aau.serg.websocketbrokerdemo.data.serverside.Player

/**
 * Reine Entscheidungs-Logik fuer die Netzwerk-Synchronisation der Wartelobby.
 *
 * [LobbyNetworkSync] ist ein Composable (UI/Side-Effects) und damit nicht
 * sinnvoll unit-testbar. Alle Entscheidungen, die es trifft, liegen hier als
 * pure, seiteneffekt-freie Funktionen -- ausserhalb der UI-Schicht und
 * vollstaendig testbar.
 */
object LobbyNetworkLogic {

    /**
     * Anzahl der gestaffelten /init-Anfragen beim Lobby-Eintritt, bis der
     * erste GameState-Broadcast eingetroffen ist.
     */
    const val RESYNC_REQUESTS = 5

    /** Abstand zwischen zwei /init-Anfragen in Millisekunden. */
    const val RESYNC_DELAY_MS = 400L

    /**
     * Darf der lokale Spieler jetzt beim Server angemeldet werden? Nur wenn er
     * "Ready" geklickt hat, einen nicht-leeren Namen hat und ein Raum aktiv ist.
     */
    fun shouldJoin(localReady: Boolean, localName: String, roomId: String): Boolean =
        localReady && localName.isNotBlank() && roomId.isNotBlank()

    /** Ist der lokale Spieler bereits in der Server-Spielerliste enthalten? */
    fun isLocalPlayerPresent(players: List<Player>, localName: String): Boolean =
        players.any { it.name == localName }

    /** Die Mitspieler (alle ausser dem lokalen Spieler) aus der Server-Liste. */
    fun remotePlayers(players: List<Player>, localName: String): List<Player> =
        players.filter { it.name != localName }

    /**
     * Soll jetzt zum GameScreen navigiert werden? Erst wenn der Countdown
     * durchgelaufen ist UND der Server das Spiel als IN_PROGRESS meldet.
     */
    fun shouldNavigateToGame(countdownComplete: Boolean, status: GameStatus?): Boolean =
        countdownComplete && status == GameStatus.IN_PROGRESS

    /**
     * Erfordert dieser Server-Fehler, dass der Spieler Name/Farbe neu waehlt
     * (= Ready zuruecksetzen)? Trifft auf Farb- und Namens-Konflikte zu.
     */
    fun requiresReselection(errorCode: ErrorCode?): Boolean =
        errorCode == ErrorCode.COLOR_ALREADY_TAKEN || errorCode == ErrorCode.NAME_ALREADY_TAKEN
}
