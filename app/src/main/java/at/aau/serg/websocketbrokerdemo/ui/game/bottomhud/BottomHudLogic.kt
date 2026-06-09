package at.aau.serg.websocketbrokerdemo.ui.game.bottomhud

import at.aau.serg.websocketbrokerdemo.data.serverside.Building
import at.aau.serg.websocketbrokerdemo.data.serverside.BuildingType
import at.aau.serg.websocketbrokerdemo.data.serverside.GameStatus
import at.aau.serg.websocketbrokerdemo.data.serverside.Player
import at.aau.serg.websocketbrokerdemo.data.serverside.PlayerColor
import at.aau.serg.websocketbrokerdemo.data.serverside.UnitType

/**
 * Pure Logik des Bottom-HUDs.
 *
 * Enthaelt die Preis-Konstanten und Ableitungen wie "kann ich das gerade
 * kaufen" / "ist meine Runde". Alle Funktionen sind seiteneffekt-frei.
 *
 * Die Preise sind aktuell hardcoded -- sobald der Server sie ueber den
 * GameState liefert (z. B. ein neues prices-Feld), koennen sie hier
 * dynamisch eingelesen werden.
 */
object BottomHudLogic {

    /** Kosten pro Einheit. */
    fun priceOf(type: UnitType): Int = when (type) {
        UnitType.INFANTRY -> 5
        UnitType.ARCHER -> 5
        UnitType.CAVALRY -> 5
        UnitType.SKELETON -> Int.MAX_VALUE // nicht kaufbar
        UnitType.BASE -> Int.MAX_VALUE     // Basis ist die Heimatfeste des Spielers - kein kaufbares Item
    }

    /** Gold-Bestand des lokalen Spielers, 0 wenn nicht gefunden. */
    fun goldOf(players: List<Player>, localName: String?): Int =
        players.firstOrNull { it.name == localName }?.gold ?: 0

    /** Farbe des lokalen Spielers, Default RED wenn nicht gefunden. */
    fun colorOf(players: List<Player>, localName: String?): PlayerColor =
        players.firstOrNull { it.name == localName }?.color ?: PlayerColor.RED

    /** Ist der lokale Spieler aktuell am Zug? */
    fun isMyTurn(currentTurn: String?, status: GameStatus, localName: String?): Boolean =
        status == GameStatus.IN_PROGRESS &&
                currentTurn != null &&
                currentTurn == localName

    /** Anzahl der Farms des lokalen Spielers. */
    fun farmCountOf(buildings: List<Building>, localName: String?): Int {
        if (localName == null) return 0
        return buildings.count { it.player == localName && it.type == BuildingType.FARM }
    }

    /** Dynamischer Farm-Preis: erste kostet 10, jede weitere +1. */
    fun farmPrice(buildings: List<Building>, localName: String?): Int {
        val count = farmCountOf(buildings, localName)
        return 10 + count
    }

    /** Darf ich eine Farm kaufen? (mit dynamischem Preis) */
    fun canBuyFarm(gold: Int, isMyTurn: Boolean, buildings: List<Building>, localName: String?): Boolean {
        if (!isMyTurn) return false
        return gold >= farmPrice(buildings, localName)
    }

    /** Darf ich gerade diese Einheit kaufen? */
    fun canBuyUnit(type: UnitType, gold: Int, isMyTurn: Boolean): Boolean =
        isMyTurn && gold >= priceOf(type)

    /** Darf ich gerade die Runde beenden? */
    fun canEndTurn(isMyTurn: Boolean): Boolean = isMyTurn
}
