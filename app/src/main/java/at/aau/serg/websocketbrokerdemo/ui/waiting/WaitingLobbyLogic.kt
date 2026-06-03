package at.aau.serg.websocketbrokerdemo.ui.waiting

import at.aau.serg.websocketbrokerdemo.ui.mainmenu.GameMode
import at.aau.serg.websocketbrokerdemo.ui.waiting.model.PlayerColor
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
     *
     * @param mode bestimmt die Anzahl der Slots (= playerCount)
     * @param nameProvider liefert den Namen fuer den lokalen Slot
     *                     (Default = zufaelliger Name aus GENERAL_NAMES;
     *                     im Test ueberschreibbar fuer Determinismus)
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
                color = PlayerColor.Red,
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
     *
     * Nuetzlich fuer die Farbwahl-UI: belegte Farben werden im
     * UI gedimmt dargestellt und sind nicht anklickbar.
     *
     * @param slots aktuelle Slot-Liste
     * @param exceptId Slot-ID, dessen Farbe NICHT als belegt zaehlt
     *                 (typischerweise der lokale Slot, weil der
     *                 seine eigene Farbe nicht "belegen" soll)
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

    /**
     * Ersetzt einen Slot in der Liste anhand seiner ID.
     *
     * @return neue Liste mit dem ersetzten Slot, oder die unveraenderte
     *         Liste wenn kein Slot mit dieser ID existiert.
     */
    fun replaceSlot(slots: List<PlayerSlot>, updated: PlayerSlot): List<PlayerSlot> =
        slots.map { if (it.id == updated.id) updated else it }

    /**
     * Aendert den Namen eines Slots.
     *
     * Wird nur durchgefuehrt, wenn der Slot noch nicht ready ist --
     * sobald ein Spieler bereit ist, ist sein Name fest.
     */
    fun applyNameChange(slots: List<PlayerSlot>, slotId: Int, newName: String): List<PlayerSlot> {
        val slot = slots.firstOrNull { it.id == slotId } ?: return slots
        if (slot.ready) return slots
        return replaceSlot(slots, slot.copy(name = newName))
    }

    /**
     * Aendert die Farbe eines Slots.
     *
     * Wird nur durchgefuehrt wenn:
     *  - Der Slot existiert
     *  - Der Slot noch nicht ready ist
     *  - Die neue Farbe nicht von einem anderen Slot belegt ist
     */
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

    /**
     * Toggelt den Ready-Status eines Slots.
     *
     * Wird nur durchgefuehrt, wenn der Slot besetzt ist UND einen Namen
     * hat. Verhindert leere Namen bei "bereit".
     */
    fun applyReadyToggle(slots: List<PlayerSlot>, slotId: Int): List<PlayerSlot> {
        val slot = slots.firstOrNull { it.id == slotId } ?: return slots
        if (slot.status == SlotStatus.Empty) return slots
        if (slot.name.isBlank()) return slots
        return replaceSlot(slots, slot.copy(ready = !slot.ready))
    }
}
