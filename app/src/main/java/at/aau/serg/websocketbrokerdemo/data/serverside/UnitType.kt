package at.aau.serg.websocketbrokerdemo.data.serverside

/**
 * Alle Einheitentypen, die der Server kennt.
 *
 * Muss exakt mit dem Server-Enum at.aau.hexabrawl.websocketserver.model.UnitType
 * uebereinstimmen, damit Gson die GameState-Updates korrekt deserialisieren kann.
 * Fehlt ein Wert, crasht die Deserialisierung der ganzen GameState-Nachricht.
 *
 *  - ARCHER     Fernkampf-Einheit. Schlaegt INFANTRY.
 *  - INFANTRY   Standard-Nahkampf-Einheit. Schlaegt CAVALRY.
 *  - CAVALRY    Schnelle Reiter-Einheit. Schlaegt ARCHER.
 *  - SKELETON   Sondereinheit (z.B. aus Schummel-Geschenken).
 *  - BASE       Die Heimatbasis des Spielers. Keine bewegliche Einheit,
 *               sondern ein stationaeres Ziel - wird sie zerstoert, ist
 *               das Spiel fuer diesen Spieler verloren.
 *
 * BEATS modelliert das Schere-Stein-Papier-Verhaeltnis zwischen den
 * normalen Kampfeinheiten und wird vom Server fuer Move/Combat-Logik
 * genutzt. Die App kann dieselbe Logik fuer Tooltips / Vorschau in der
 * UI nutzen, ohne Server-Round-Trip.
 */
enum class UnitType {
    ARCHER,
    INFANTRY,
    CAVALRY,
    SKELETON,
    BASE;

    companion object {
        val BEATS = mapOf(INFANTRY to CAVALRY, CAVALRY to ARCHER, ARCHER to INFANTRY)
    }

    fun beats(other: UnitType) = BEATS[this] == other
}
