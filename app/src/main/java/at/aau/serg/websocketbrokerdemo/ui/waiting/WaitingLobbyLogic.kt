package at.aau.serg.websocketbrokerdemo.ui.waiting

import at.aau.serg.websocketbrokerdemo.data.serverside.PlayerColor
import at.aau.serg.websocketbrokerdemo.ui.mainmenu.GameMode
import at.aau.serg.websocketbrokerdemo.ui.waiting.model.PlayerSlot
import at.aau.serg.websocketbrokerdemo.ui.waiting.model.SlotStatus

/**
 * Pure Logik fuer die Wartelobby.
 *
 * Alle hier definierten Funktionen sind seiteneffekt-frei und ohne
 * Compose-Runtime testbar. Das ViewModel ruft sie auf und verwaltet
 * den daraus resultierenden State.
 */
object WaitingLobbyLogic {

    /** Verfuegbare General-Namen fuer den initialen Vorschlag. */
    val GENERAL_NAMES: List<String> = listOf(
        "General Aldric", "General Borian", "General Cassia", "General Domitian",
        "General Eolyn", "General Faramond", "General Greta", "General Hadrik",
        "General Isolde", "General Joren", "General Kaethe", "General Leofric",
        "General Mira", "General Nikolaus", "General Ortrun",
        "Lord-Marshal Quentin", "Hauptmann Reinhart", "Feldherr Sigmund",
        "Marschall Theodora", "General Ulric"
    )

    /**
     * Liefert die initiale Slot-Liste fuer einen [GameMode].
     *
     *  - Slot 0 ist der lokale Spieler (mit zufaelligem General-Namen)
     *  - Slots 1..n sind leer und warten auf andere Spieler
     */
    fun createInitialSlots(
        mode: GameMode,
        nameProvider: () -> String = { GENERAL_NAMES.random() }
    ): List<PlayerSlot> {
        val slots = mutableListOf<PlayerSlot>()
        slots.add(
            PlayerSlot(
                id = 0,
                status = SlotStatus.Player,
                name = nameProvider(),
                color = PlayerColor.RED,
                ready = false,
                isLocal = true
            )
        )
        for (i in 1 until mode.playerCount) {
            slots.add(PlayerSlot(id = i))
        }
        return slots
    }

    /**
     * Liefert die Farben, die bereits von anderen Slots belegt sind.
     */
    fun takenColorsExcept(slots: List<PlayerSlot>, exceptId: Int): Set<PlayerColor> =
        slots
            .filter { it.id != exceptId && it.status != SlotStatus.Empty }
            .map { it.color }
            .toSet()

    /** Pruefe ob alle Slots besetzt UND bereit sind. */
    fun allReady(slots: List<PlayerSlot>): Boolean =
        slots.isNotEmpty() && slots.all {
            it.status != SlotStatus.Empty && it.ready
        }

    /** Ersetzt einen Slot in der Liste anhand seiner ID. */
    fun replaceSlot(slots: List<PlayerSlot>, updated: PlayerSlot): List<PlayerSlot> =
        slots.map { if (it.id == updated.id) updated else it }

    /** Aendert den Namen eines Slots (nur wenn nicht bereit). */
    fun applyNameChange(slots: List<PlayerSlot>, slotId: Int, newName: String): List<PlayerSlot> {
        val slot = slots.firstOrNull { it.id == slotId } ?: return slots
        if (slot.ready) return slots
        return replaceSlot(slots, slot.copy(name = newName))
    }

    /** Aendert die Farbe eines Slots (nur wenn nicht bereit und Farbe frei). */
    fun applyColorChange(
        slots: List<PlayerSlot>,
        slotId: Int,
        newColor: PlayerColor
    ): List<PlayerSlot> {
        val slot = slots.firstOrNull { it.id == slotId } ?: return slots
        if (slot.ready) return slots
        val taken = takenColorsExcept(slots, slotId)
        if (newColor in taken) return slots
        return replaceSlot(slots, slot.copy(color = newColor))
    }

    /** Toggelt den Ready-Status (nur bei besetztem Slot mit Namen). */
    fun applyReadyToggle(slots: List<PlayerSlot>, slotId: Int): List<PlayerSlot> {
        val slot = slots.firstOrNull { it.id == slotId } ?: return slots
        if (slot.status == SlotStatus.Empty) return slots
        if (slot.name.isBlank()) return slots
        return replaceSlot(slots, slot.copy(ready = !slot.ready))
    }
}
