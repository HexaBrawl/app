package at.aau.serg.websocketbrokerdemo.data.serverside

/**
 * Ein einzelnes Hex-Feld auf der Spielkarte.
 *
 * Wird vom Server als Teil des GameState (Liste fields) mitgeschickt
 * und beschreibt, wem das Feld aktuell gehoert. Die App nutzt das,
 * um eroberte Gebiete in der Spielerfarbe einzufaerben.
 *
 * Felder:
 *  - x           Spalten-Koordinate auf der Hex-Map (siehe Server-Mapgenerator).
 *  - y           Zeilen-Koordinate auf der Hex-Map.
 *  - owner       Name des Spielers, dem das Feld gehoert,
 *                oder null wenn das Feld neutral / unbeansprucht ist.
 *  - isSkeleton  Markiert ein Feld, das vom Hauptgebiet des Spielers
 *                abgeschnitten ist (vom Server berechnet via
 *                recomputeConnectivity). Owner bleibt erhalten -- der
 *                Spieler kann das Feld zurueckerobern -- aber Skelett-
 *                Felder geben kein Income pro Runde. Default false.
 *
 * Muss mit dem Server-DTO at.aau.hexabrawl.websocketserver.model.Field
 * uebereinstimmen, damit Gson die GameState-Updates korrekt
 * deserialisieren kann.
 */
data class Field(
    val x: Int = 0,
    val y: Int = 0,
    var owner: String? = null,
    var isSkeleton: Boolean = false
)
