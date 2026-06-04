package at.aau.serg.websocketbrokerdemo.ui.game.tophud

import at.aau.serg.websocketbrokerdemo.data.serverside.Player

/**
 * Pure Logik des Top-HUDs.
 *
 * Reduziert den Server-State auf die im HUD angezeigten Werte des
 * lokalen Spielers. Seiteneffekt-frei und damit unit-testbar.
 */
object TopHudLogic {

    /** Gold-Bestand des lokalen Spielers, 0 wenn nicht gefunden. */
    fun goldFor(players: List<Player>, localName: String?): Int =
        players.firstOrNull { it.name == localName }?.gold ?: 0

    /** Einkommen des lokalen Spielers pro Runde, 0 wenn nicht gefunden. */
    fun incomeFor(players: List<Player>, localName: String?): Int =
        players.firstOrNull { it.name == localName }?.income ?: 0
}
