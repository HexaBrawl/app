package at.aau.serg.websocketbrokerdemo.ui.game

/**
 * Konstanten fuer den Resync-Mechanismus nach (Re-)Connect.
 *
 * Wird vom [GameStateResyncEffect] genutzt. Liegt in einer eigenen
 * Datei, damit die Spielregel-Werte nicht in der Composable haengen
 * und direkt JVM-testbar bleiben.
 *
 * Die Werte folgen dem Pattern der Wartelobby (siehe LobbyNetworkSync):
 *  - mehrere gestaffelte /init-Anfragen, statt nur einer
 *  - kurzer Delay dazwischen, damit der Server Zeit zum Broadcast hat
 *  - Abbruch sobald ein frischer Broadcast eintrifft (von aussen)
 */
object GameStateResyncLogic {

    /** Anzahl gestaffelter /init-Anfragen fuer den Resync nach (Re-)Connect. */
    const val RESYNC_REQUESTS = 5

    /** Abstand zwischen zwei Resync-Anfragen in Millisekunden. */
    const val RESYNC_DELAY_MS = 400L
}
