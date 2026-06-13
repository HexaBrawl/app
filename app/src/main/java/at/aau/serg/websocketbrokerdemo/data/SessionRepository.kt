package at.aau.serg.websocketbrokerdemo.data

/**
 * In-Memory-Store fuer die Spieler-Identitaet im laufenden Match.
 *
 * Wird gebraucht, damit Reconnect- und Leave-Aufrufe den Server-Endpoints
 * dieselben Daten mitgeben koennen, die der initiale Join verwendet hat
 * — also den Spielernamen, den 6-stelligen Raum-Code und die Raum-UUID.
 *
 * Werte werden bewusst NICHT persistiert. Sprint-3-Scope: App-Kill =
 * Spieler weg. Wer den Prozess killt, ist auch fuer den Server raus —
 * der Reconnect deckt nur kurze WebSocket-Drops ab (Display-Off,
 * Netzwechsel, Cell-Tower-Wechsel).
 *
 * Wiring: wird in [at.aau.serg.websocketbrokerdemo.MainActivity]
 * instanziiert und ueber [at.aau.serg.websocketbrokerdemo.network.GameSession]
 * an die ViewModels gereicht (kein DI-Framework im Projekt).
 */
class SessionRepository {

    /** Anzeigename des lokalen Spielers (in der Wartelobby bestaetigt). */
    var playerName: String? = null

    /** 6-Zeichen-Join-Code des aktuellen Raums (Anzeige + Reconnect-Payload). */
    var joinCode: String? = null

    /** UUID des aktuellen Raums (Pfad-Segment fuer STOMP-Topics). */
    var roomId: String? = null

    /** Setzt alle Felder zurueck. Aufruf nach Spielende oder /leave. */
    fun clear() {
        playerName = null
        joinCode = null
        roomId = null
    }
}
