package at.aau.serg.websocketbrokerdemo.ui.game.tophud

import at.aau.serg.websocketbrokerdemo.data.serverside.PendingGift
import at.aau.serg.websocketbrokerdemo.data.serverside.Player
import kotlin.random.Random

/**
 * Pure Logik fuer die Schummel-Geschenk-Mechanik.
 *
 * Seiteneffekt-frei (bis auf [rollDelta], das wahlweise einen eigenen
 * RNG annehmen kann -- praktisch fuer Tests). Damit das ViewModel klein
 * und gut testbar bleibt.
 */
object CheatGiftLogic {

    /** Wieviele Klicks aufs Geschenk-Icon noetig sind bevor's ausgeloest wird. */
    const val CLICKS_TO_TRIGGER = 5

    /** Minimum-Delta (negativ = Verlust). */
    const val MIN_DELTA = -10

    /** Maximum-Delta (positiv = Gewinn). */
    const val MAX_DELTA = 10

    /**
     * Wuerfelt das Delta fuer das Geschenk -- zwischen [MIN_DELTA] und
     * [MAX_DELTA] (inklusiv). Der Wert kann negativ sein, also kann der
     * Spieler beim Geschenk auch Gold verlieren.
     */
    fun rollDelta(random: Random = Random.Default): Int =
        random.nextInt(MIN_DELTA, MAX_DELTA + 1)

    /**
     * Pruefe ob der lokale Spieler den Schummel-Button noch benutzen
     * darf. Server-Truth: Player.hasUsedGift.
     */
    fun canUseGift(players: List<Player>, localName: String?): Boolean {
        val me = players.firstOrNull { it.name == localName } ?: return false
        return !me.hasUsedGift
    }

    /**
     * Pruefe ob der lokale Spieler gerade das Steal-Popup sehen soll.
     * Bedingungen:
     *  - Es gibt ein pendingGift
     *  - Der lokale Spieler ist NICHT der Owner (Owner sieht das
     *    WaitingOverlay)
     *  - Der lokale Spieler hat noch nicht "Ja" oder "Nein" gedrueckt
     *    (das wird vom ViewModel getrackt, nicht vom State hier)
     */
    fun shouldShowStealPopup(
        pendingGift: PendingGift?,
        localName: String?
    ): Boolean {
        if (pendingGift == null) return false
        if (localName == null) return false
        return pendingGift.ownerName != localName
    }

    /**
     * Pruefe ob der lokale Spieler das Waiting-Overlay sehen soll
     * (Owner-Sicht: "warte auf Gegner").
     */
    fun shouldShowWaitingOverlay(
        pendingGift: PendingGift?,
        localName: String?
    ): Boolean {
        if (pendingGift == null) return false
        return pendingGift.ownerName == localName
    }
}
