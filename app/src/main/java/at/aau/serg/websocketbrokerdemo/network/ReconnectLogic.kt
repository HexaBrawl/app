package at.aau.serg.websocketbrokerdemo.network

import kotlinx.coroutines.delay

/**
 * Pure Retry-Schleife fuer den Auto-Reconnect.
 *
 * Loest die Network-Klasse [Stomp] von der eigentlichen Schleifenlogik,
 * damit das "wieviele Versuche im welchen Abstand"-Verhalten ohne
 * StompClient-Mock unit-testbar bleibt: [Stomp] uebergibt eine
 * suspendierende `attempt`-Funktion und einen Delay-Hook; der Test
 * uebergibt eine Liste von booleschen Antworten und prueft Anzahl + Reihenfolge.
 */
object ReconnectLogic {

    /**
     * Versucht maximal [maxAttempts] Mal einen Connect.
     *
     * Bricht ab und liefert true, sobald [attempt] true zurueck gibt.
     * Zwischen zwei Versuchen wartet die Funktion [delayMillis]
     * Millisekunden. Liefert false wenn alle Versuche fehlschlagen.
     *
     * @param attempt   Connect-Logik. true = erfolgreich (Schleife endet),
     *                  false = noch nicht verbunden (weiterer Versuch).
     * @param delayFn   Wartefunktion zwischen zwei Versuchen.
     *                  Default delegiert an [kotlinx.coroutines.delay];
     *                  Tests koennen das ueberschreiben um virtuelle
     *                  Zeit zu sparen.
     */
    suspend fun retryUntilSuccess(
        maxAttempts: Int,
        delayMillis: Long,
        attempt: suspend () -> Boolean,
        delayFn: suspend (Long) -> Unit = ::delay
    ): Boolean {
        require(maxAttempts >= 1) { "maxAttempts must be >= 1, was $maxAttempts" }
        for (i in 1..maxAttempts) {
            if (attempt()) return true
            if (i < maxAttempts) delayFn(delayMillis)
        }
        return false
    }
}
