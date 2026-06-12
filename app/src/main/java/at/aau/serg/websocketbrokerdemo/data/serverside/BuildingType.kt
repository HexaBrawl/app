package at.aau.serg.websocketbrokerdemo.data.serverside

/**
 * Gebaeudeklassen die ein Spieler auf seinem Gebiet errichten kann.
 *
 * Werte entsprechen dem Server-Enum
 * `at.aau.hexabrawl.websocketserver.model.BuildingType`, damit Gson die
 * JSON-Strings ohne Mapping deserialisiert.
 */
enum class BuildingType {
    /** Heimatburg eines Spielers; Verlust beendet das Spiel. */
    CASTLE,

    /** Farm — generiert pro Runde zusaetzliches Gold-Einkommen. */
    FARM
}
