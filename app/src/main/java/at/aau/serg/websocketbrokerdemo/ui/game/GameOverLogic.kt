package at.aau.serg.websocketbrokerdemo.ui.game

import at.aau.serg.websocketbrokerdemo.data.serverside.GameStatus
import at.aau.serg.websocketbrokerdemo.data.serverside.Player

/**
 * Reine Entscheidungs-Logik fuer das Spielende.
 *
 * [GameOverNavigationEffect] ist ein Composable (Navigation/Side-Effect) und
 * damit nicht unit-testbar. Die Entscheidungen "ist das Spiel vorbei?" und
 * "habe ich gewonnen?" liegen hier als pure, seiteneffekt-freie Funktionen.
 */
object GameOverLogic {

    /**
     * Ist das Spiel fuer den lokalen Spieler vorbei?
     *  - klassisches Spielende ([GameStatus.FINISHED]), oder
     *  - der lokale Spieler ist nicht mehr in [players] (Eliminierung im
     *    3-/4-Spieler-Modus; der Server entfernt Ausgeschiedene aus der Liste).
     */
    fun isGameOver(players: List<Player>, status: GameStatus, localName: String): Boolean =
        status == GameStatus.FINISHED || players.none { it.name == localName }

    /** Hat der lokale Spieler gewonnen? (Sieger-Name == lokaler Name.) */
    fun isLocalWinner(winner: String?, localName: String): Boolean =
        winner == localName
}
