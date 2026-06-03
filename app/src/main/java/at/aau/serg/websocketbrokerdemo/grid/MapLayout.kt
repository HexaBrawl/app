package at.aau.serg.websocketbrokerdemo.grid

/**
 * Beschreibt die Geometrie einer Spielkarte fuer einen bestimmten
 * Spielmodus.
 *
 *  - [rows] und [cols] bestimmen, wieviele Hex-Zellen das Brett hat.
 *  - [hexSize] ist die Kantenlaenge eines Hex in Pixeln. Kleinere
 *    hexSize bei groesseren Karten, damit das gesamte Brett noch auf
 *    den Bildschirm passt.
 *  - [name] dient nur zur Identifikation/Anzeige (z. B. fuer Debug).
 */
data class MapLayout(
    val rows: Int,
    val cols: Int,
    val hexSize: Float,
    val name: String
)
