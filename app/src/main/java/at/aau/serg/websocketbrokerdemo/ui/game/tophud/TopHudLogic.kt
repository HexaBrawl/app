package at.aau.serg.websocketbrokerdemo.ui.game.tophud

import at.aau.serg.websocketbrokerdemo.data.serverside.Player

/**
 * Pure Logik des Top-HUDs.
 *
 * Reduziert den Server-State auf die im HUD angezeigten Werte des
 * lokalen Spielers. Seiteneffekt-frei und damit unit-testbar.
 */
object TopHudLogic {

    /**
     * Gold pro Farm pro Runde -- spiegelt FARM_INCOME_PER_ROUND im
     * Server (siehe GameService.kt). Wird vom Income-Detail-Popup
     * angezeigt; bei Aenderung im Backend hier nachziehen.
     */
    const val GOLD_PER_FARM = 3

    /**
     * Gold pro besessenem Feld pro Runde -- spiegelt FIELD_INCOME_PER_ROUND
     * im Server (siehe GameService.kt). Skelett-Felder zaehlen nicht.
     * Wird vom Income-Detail-Popup angezeigt; bei Aenderung im Backend
     * hier nachziehen.
     */
    const val GOLD_PER_FIELD = 1

    /** Gold-Bestand des lokalen Spielers, 0 wenn nicht gefunden. */
    fun goldFor(players: List<Player>, localName: String?): Int =
        players.firstOrNull { it.name == localName }?.gold ?: 0

    /** Brutto-Einkommen des lokalen Spielers pro Runde, 0 wenn nicht gefunden. */
    fun incomeFor(players: List<Player>, localName: String?): Int =
        players.firstOrNull { it.name == localName }?.income ?: 0

    /**
     * Netto-Einkommen des lokalen Spielers pro Runde (income - upkeep).
     * Spiegelt den tatsaechlichen Gold-Zuwachs am Rundenende. Negativ
     * heisst Insolvenz -- alle Truppen werden zu Skeletten.
     */
    fun netIncomeFor(players: List<Player>, localName: String?): Int {
        val player = players.firstOrNull { it.name == localName } ?: return 0
        return player.income - player.upkeep
    }
}
