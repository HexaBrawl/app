package at.aau.serg.websocketbrokerdemo.ui.mainmenu

/**
 * Helfer-Klasse fuer Lookups rund um den [GameMode]-Enum.
 *
 * Hier liegen ausschliesslich Lookup-Operationen, die der Enum selbst
 * nicht ausfuehren soll, damit die Daten-Definition (siehe GameMode)
 * frei von Logik bleibt.
 *
 * Routen-Mapping (GameMode -> Screen) ist NICHT hier, sondern lebt
 * in den jeweiligen Logic-Klassen der nutzenden Features
 * (MainMenuLogic, LobbyLogic) -- jede Funktion an genau einer
 * passenden Stelle.
 */
object GameModeLogic {

    /**
     * Lookup nach Spieleranzahl.
     *
     * @return passender Modus oder null, wenn keine Variante diese
     *         Spielerzahl unterstuetzt (z. B. Solo oder mehr als 4).
     */
    fun byPlayerCount(count: Int): GameMode? =
        GameMode.entries.firstOrNull { it.playerCount == count }
}
