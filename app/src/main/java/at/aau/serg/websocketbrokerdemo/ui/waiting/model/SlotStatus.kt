package at.aau.serg.websocketbrokerdemo.ui.waiting.model

/**
 * Status eines Slots in der Wartelobby.
 *
 *  - [Empty]   Niemand in diesem Slot (UI: "Warte auf Verbuendeten...")
 *  - [Player]  Slot ist von einem echten Spieler besetzt
 */
enum class SlotStatus {
    Empty,
    Player
}
