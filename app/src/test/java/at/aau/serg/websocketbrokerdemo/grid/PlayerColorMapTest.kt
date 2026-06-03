package at.aau.serg.websocketbrokerdemo.grid

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Tests fuer PlayerColorMap.
 *
 * Wichtigstes Property: deterministisch (gleicher Name -> gleiche
 * Farbe, auf allen Geraeten und nach App-Restart).
 */
class PlayerColorMapTest {

    @Test
    fun `same name always returns same color`() {
        val a = PlayerColorMap.colorFor("Alice")
        val b = PlayerColorMap.colorFor("Alice")
        assertEquals(a, b)
    }

    @Test
    fun `different names usually get different colors`() {
        // Bei 4-Farb-Palette und 4 unterschiedlichen Namen koennen
        // theoretisch Kollisionen passieren. Wir nutzen Namen, deren
        // hashCodes mod 4 alle Werte abdecken.
        // Stattdessen pruefen wir nur: nicht alle Spieler haben die
        // gleiche Farbe (= Deterministic-Hash funktioniert).
        val names = listOf("Alice", "Bob", "Carol", "Dave", "Eve", "Frank")
        val colors = names.map { PlayerColorMap.colorFor(it) }.toSet()
        assertTrue(colors.size > 1, "Mit 6 Namen muessen mind. 2 Farben rauskommen")
    }

    @Test
    fun `colors come from a fixed palette`() {
        val red = android.graphics.Color.RED
        val blue = android.graphics.Color.BLUE
        val green = android.graphics.Color.GREEN
        val yellow = android.graphics.Color.YELLOW
        val palette = setOf(red, blue, green, yellow)

        val sampleNames = listOf("X", "Y", "Z", "A", "B", "C", "D", "E")
        sampleNames.forEach { name ->
            assertTrue(
                PlayerColorMap.colorFor(name) in palette,
                "Farbe fuer $name muss aus der Palette stammen"
            )
        }
    }

    @Test
    fun `empty string returns a valid palette color`() {
        // Edge-case: leerer Name darf nicht crashen oder Color.TRANSPARENT
        // liefern.
        val color = PlayerColorMap.colorFor("")
        val palette = setOf(
            android.graphics.Color.RED,
            android.graphics.Color.BLUE,
            android.graphics.Color.GREEN,
            android.graphics.Color.YELLOW
        )
        assertTrue(color in palette)
    }

    @Test
    fun `mapping is independent of call order`() {
        // Beweist dass keine versteckte State-Mutation am Werk ist --
        // die Reihenfolge der Aufrufe darf das Ergebnis nicht aendern.
        val firstAlice = PlayerColorMap.colorFor("Alice")
        repeat(10) { PlayerColorMap.colorFor("DummyPlayer-$it") }
        val secondAlice = PlayerColorMap.colorFor("Alice")
        assertEquals(firstAlice, secondAlice)
    }
}
