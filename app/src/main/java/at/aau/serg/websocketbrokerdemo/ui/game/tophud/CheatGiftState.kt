package at.aau.serg.websocketbrokerdemo.ui.game.tophud

/**
 * UI-State fuer die Schummel-Geschenk-Mechanik.
 *
 *  - [clickCount]      Lokaler Counter der Klicks aufs Geschenk-Icon.
 *                      Wird beim Erreichen von 5 zurueckgesetzt und an
 *                      den Server gesendet. Lokal weil der Server eh
 *                      nur das Endergebnis interessiert; pro Klick
 *                      einen Roundtrip waere overkill.
 *  - [hasResponded]    Ob der lokale Spieler auf das aktuelle Steal-
 *                      Popup schon mit Ja/Nein reagiert hat. Wird
 *                      lokal getrackt, damit der Popup nach dem Klick
 *                      verschwindet, ohne aufs Server-State zu warten.
 *                      Wird reset wenn pendingGift im GameState weg ist.
 */
data class CheatGiftState(
    val clickCount: Int = 0,
    val hasResponded: Boolean = false
)
