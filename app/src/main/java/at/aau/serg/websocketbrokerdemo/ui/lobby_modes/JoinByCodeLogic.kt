package at.aau.serg.websocketbrokerdemo.ui.lobby_modes

/**
 * Reine Logik fuer den "Beitreten via Code"-Dialog.
 *
 * Sammelt zwei Aspekte, die im Composable mischen wuerden:
 *  1. Eingabe-Normalisierung (nur Buchstaben/Ziffern, max. 8 Zeichen,
 *     UPPERCASE)
 *  2. Gueltigkeits-Pruefung (Code muss 4 bis 8 Zeichen lang sein)
 *
 * Beide Funktionen sind seiteneffekt-frei und damit ohne Compose-
 * Runtime testbar -- der Dialog selbst ruft sie nur auf.
 */
object JoinByCodeLogic {

    /** Erlaubte Code-Laenge (inklusive). */
    const val MIN_LENGTH = 4
    const val MAX_LENGTH = 8

    /**
     * Normalisiert eine Code-Eingabe.
     *
     *  - Filtert alle nicht-alphanumerischen Zeichen heraus
     *  - Kuerzt auf maximal [MAX_LENGTH] Zeichen
     *  - Konvertiert zu Grossbuchstaben
     *
     * Wird bei jedem `onValueChange` des TextField aufgerufen, damit der
     * User keine ungueltigen Zeichen eingeben kann (z. B. Leerzeichen,
     * Emoji, ...).
     */
    fun normalize(input: String): String =
        input.filter { it.isLetterOrDigit() }
            .take(MAX_LENGTH)
            .uppercase()

    /**
     * Prueft ob ein bereits normalisierter Code akzeptabel ist.
     *
     * Wird zur Aktivierung des "Beitreten"-Buttons genutzt.
     */
    fun isValid(code: String): Boolean =
        code.length in MIN_LENGTH..MAX_LENGTH
}
