package at.aau.serg.websocketbrokerdemo.ui.waiting.model

/**
 * Repraesentiert einen Slot in der Wartelobby.
 *
 * Ein Slot kann entweder leer sein (status = Empty) oder von einem
 * Spieler besetzt (status = Player). Der lokale Spieler (eigenes Geraet)
 * hat zusaetzlich isLocal = true und darf seinen Namen, seine Farbe und
 * den Ready-Status veraendern; remote Slots sind read-only.
 *
 * Sobald `ready == true` ist, zeigt das Wachssiegel den "bereit"-Status
 * an und der Countdown-Timer kann starten, wenn alle anderen Slots
 * ebenfalls bereit sind.
 */
data class PlayerSlot(
    val id: Int,
    val status: SlotStatus = SlotStatus.Empty,
    val name: String = "",
    val color: PlayerColor = PlayerColor.Red,
    val ready: Boolean = false,
    val isLocal: Boolean = false
)
