package at.aau.serg.websocketbrokerdemo.ui.game.tophud

/**
 * View-State fuers Einkommen-Detail-Popup.
 *
 * Wird von [IncomeBreakdownLogic] aus dem Server-GameState abgeleitet
 * und vom [components.IncomeDetailsPopup] angezeigt. Reine Datenklasse
 * ohne Compose-Abhaengigkeit -- damit ist die Berechnung in
 * [IncomeBreakdownLogic] ohne Compose-Runtime testbar.
 *
 * Aufbau spiegelt den Server (siehe GameService.computeIncome /
 * computeUpkeep):
 *  - Brutto = farms * goldPerFarm + fields * goldPerField
 *  - Upkeep = (0 until units).sumOf { 3 + it }   -> (3+4+5+...)
 *  - Netto  = Brutto - Upkeep
 *
 * Felder:
 *  - farms         Anzahl Farmen des lokalen Spielers (Server-Truth).
 *  - goldPerFarm   Gold pro Farm pro Runde (Konstante, spiegelt
 *                  FARM_INCOME_PER_ROUND im Server).
 *  - farmIncome    Brutto-Einkommen aus Farmen: farms * goldPerFarm.
 *  - fields        Anzahl Income-gebender Felder des Spielers.
 *                  Skelett-Felder gehoeren ihm noch, geben aber kein
 *                  Gold und zaehlen hier nicht.
 *  - goldPerField  Gold pro Feld pro Runde (Konstante, spiegelt
 *                  FIELD_INCOME_PER_ROUND im Server).
 *  - fieldIncome   Brutto-Einkommen aus Feldern: fields * goldPerField.
 *  - grossIncome   Brutto-Einkommen total = farmIncome + fieldIncome.
 *                  Sollte mit Player.income vom Server uebereinstimmen
 *                  -- Konsistenz-Pruefung im Test.
 *  - units         Anzahl Kampf-Einheiten (ohne BASE und SKELETON),
 *                  fuer die Unterhalt anfaellt.
 *  - upkeep        Truppenunterhalt pro Runde, Server-Truth via
 *                  Player.upkeep.
 *  - netIncome     Netto pro Runde = grossIncome - upkeep. Negative
 *                  Werte deuten auf drohende Insolvenz hin (alle
 *                  Truppen werden zu Skeletten).
 */
data class IncomeBreakdown(
    val farms: Int = 0,
    val goldPerFarm: Int = 0,
    val farmIncome: Int = 0,
    val fields: Int = 0,
    val goldPerField: Int = 0,
    val fieldIncome: Int = 0,
    val grossIncome: Int = 0,
    val units: Int = 0,
    val upkeep: Int = 0,
    val netIncome: Int = 0
)
