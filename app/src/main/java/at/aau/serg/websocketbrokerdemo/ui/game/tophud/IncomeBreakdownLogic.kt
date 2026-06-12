package at.aau.serg.websocketbrokerdemo.ui.game.tophud

import at.aau.serg.websocketbrokerdemo.data.serverside.Field
import at.aau.serg.websocketbrokerdemo.data.serverside.GameUnit
import at.aau.serg.websocketbrokerdemo.data.serverside.Player
import at.aau.serg.websocketbrokerdemo.data.serverside.UnitType

/**
 * Pure Logik fuers Income-Detail-Popup.
 *
 * Baut aus dem Server-State den View-State [IncomeBreakdown] fuer den
 * lokalen Spieler. Seiteneffekt-frei und ohne Compose-Runtime, damit
 * die Berechnung ohne Composable-Setup testbar ist.
 *
 * Source-of-Truth-Aufteilung:
 *  - grossIncome und upkeep kommen DIREKT vom Server (Player.income
 *    bzw. Player.upkeep) -- da gibt es keine Doppel-Rechnung.
 *  - farmIncome und fieldIncome werden client-side fuer die Anzeige
 *    aufgeschluesselt (das Popup zeigt "Farms 2 x 3 Gold = +6").
 *  - Drift-Test (siehe IncomeBreakdownLogicTest) prueft, dass
 *    farmIncome + fieldIncome == player.income gilt -- falls die
 *    Backend-Konstante sich aendert, schlaegt der Test sofort an.
 *
 * Regeln spiegeln den Server (siehe GameService.computeIncome /
 * computeUpkeep):
 *  - Felder mit `isSkeleton = true` zaehlen NICHT zum Einkommen
 *    (gehoeren dem Spieler weiter, geben aber kein Gold).
 *  - Truppen-Unterhalt wird nur fuer Kampf-Einheiten gezahlt --
 *    BASE und SKELETON sind ausgenommen.
 */
object IncomeBreakdownLogic {

    /**
     * Liefert den View-State fuers Income-Popup.
     *
     * Bei unbekanntem [localName] (z.B. Spieler noch nicht im Raum)
     * wird ein leerer Breakdown mit Nullwerten und den Spielregel-
     * Konstanten zurueckgegeben -- das Popup soll sich nicht
     * weigern zu rendern.
     */
    fun buildBreakdown(
        players: List<Player>,
        units: List<GameUnit>,
        fields: List<Field>,
        localName: String?
    ): IncomeBreakdown {
        val player = players.firstOrNull { it.name == localName }
            ?: return IncomeBreakdown(
                goldPerFarm = TopHudLogic.GOLD_PER_FARM,
                goldPerField = TopHudLogic.GOLD_PER_FIELD
            )

        val fieldCount = fields.count { it.owner == localName && !it.isSkeleton }
        val unitCount = units.count {
            it.player == localName &&
                it.type != UnitType.SKELETON &&
                it.type != UnitType.BASE
        }

        val farmIncome = player.farms * TopHudLogic.GOLD_PER_FARM
        val fieldIncome = fieldCount * TopHudLogic.GOLD_PER_FIELD

        return IncomeBreakdown(
            farms = player.farms,
            goldPerFarm = TopHudLogic.GOLD_PER_FARM,
            farmIncome = farmIncome,
            fields = fieldCount,
            goldPerField = TopHudLogic.GOLD_PER_FIELD,
            fieldIncome = fieldIncome,
            grossIncome = player.income,
            units = unitCount,
            upkeep = player.upkeep,
            netIncome = player.income - player.upkeep
        )
    }
}
