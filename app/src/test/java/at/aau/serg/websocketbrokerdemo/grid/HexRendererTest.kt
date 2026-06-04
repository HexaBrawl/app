package at.aau.serg.websocketbrokerdemo.grid

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import at.aau.serg.websocketbrokerdemo.data.serverside.GameUnit
import at.aau.serg.websocketbrokerdemo.data.serverside.Player
import at.aau.serg.websocketbrokerdemo.data.serverside.PlayerColor
import at.aau.serg.websocketbrokerdemo.data.serverside.UnitType
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests fuer HexRenderer.
 *
 * Compose-Renderer testen wir ueber MockK auf dem DrawScope-Interface --
 * wir koennen kein echtes Pixel-Output verifizieren, aber wir koennen
 * pruefen welche draw-Aufrufe in welcher Reihenfolge gemacht werden.
 *
 * Was wir testen:
 *  - Anzahl der drawPath-Aufrufe entspricht Zellenzahl
 *  - Icons werden pro Einheit gezeichnet
 *  - Einheiten-Farbe stimmt mit PlayerColor ueberein (Lookup des richtigen Painters)
 *
 * Was wir NICHT testen koennen (Compose-Limit):
 *  - Genaue Pixel-Koordinaten der Pfade
 *  - Visuelle Korrektheit
 *
 * Die rein-mathematischen Anteile (cellCenter, hexCorners) sind in
 * HexGridLogicTest separat abgedeckt.
 */
class HexRendererTest {

    private val tinyLayout = MapLayout(rows = 2, cols = 2, hexSize = 10f, name = "tiny")

    private val alice = Player(name = "Alice", color = PlayerColor.RED)
    private val bob = Player(name = "Bob", color = PlayerColor.BLUE)

    private lateinit var renderer: HexRenderer
    private lateinit var scope: DrawScope
    private lateinit var unitPainters: Map<Pair<PlayerColor, UnitType>, Painter>

    @BeforeEach
    fun setUp() {
        renderer = HexRenderer()
        scope = mockk(relaxed = true)
        
        unitPainters = PlayerColor.entries.flatMap { color ->
            UnitType.entries.map { type ->
                (color to type) to mockk<Painter>(relaxed = true)
            }
        }.toMap()

        // Die wichtigsten DrawScope-Aufrufe explizit stubben, damit
        // verify auf sie greifen kann.
        justRun { scope.drawPath(any(), any<Color>(), any(), any(), any(), any()) }
    }

    private fun draw(units: List<GameUnit> = emptyList(), players: List<Player> = emptyList()) {
        with(renderer) {
            scope.render(tinyLayout, units, players, unitPainters)
        }
    }

    // ---- Hex-Pfade ------------------------------------------------------

    @Test
    fun `draws one hex path per cell`() {
        // 2x2 Layout = 4 Zellen = 4 drawPath-Aufrufe (jeweils der Hex-Rand).
        draw()
        verify(exactly = 4) {
            scope.drawPath(any(), any<Color>(), any(), any(), any(), any())
        }
    }

    @Test
    fun `hex paths are drawn with black stroke`() {
        // Jede Zelle bekommt einen schwarzen Rand mit Stroke-Style.
        val colorSlot = slot<Color>()
        every {
            scope.drawPath(any(), capture(colorSlot), any(), any(), any(), any())
        } answers { /* ignore */ }

        draw()

        // Letzter Aufruf war schwarz; alle vorigen auch (alle identisch)
        verify(exactly = 4) {
            scope.drawPath(any<Path>(), Color.Black, any(), Stroke(3f), any(), any())
        }
    }

    // ---- Units ---------------------------------------------------------

    @Test
    fun `draws one icon per unit`() {
        val units = listOf(
            GameUnit(player = "Alice", x = 0, y = 0, type = UnitType.INFANTRY),
            GameUnit(player = "Bob", x = 1, y = 1, type = UnitType.CAVALRY)
        )
        draw(units = units, players = listOf(alice, bob))

        val alicePainter = unitPainters[PlayerColor.RED to UnitType.INFANTRY]!!
        val bobPainter = unitPainters[PlayerColor.BLUE to UnitType.CAVALRY]!!

        verify(exactly = 1) {
            with(alicePainter) { scope.draw(any()) }
            with(bobPainter) { scope.draw(any()) }
        }
    }

    @Test
    fun `uses red painter for red unit`() {
        val units = listOf(GameUnit(player = "Alice", x = 0, y = 0, type = UnitType.INFANTRY))
        draw(units = units, players = listOf(alice))

        val redPainter = unitPainters[PlayerColor.RED to UnitType.INFANTRY]!!
        verify {
            with(redPainter) { scope.draw(any()) }
        }
    }

    @Test
    fun `uses fallback color when unit player is not in players list`() {
        // Edge-Case: Unit-Owner ist nicht in der Spieler-Liste.
        // Sollte PlayerColor.RED als Fallback nutzen.
        val units = listOf(GameUnit(player = "Ghost", x = 0, y = 0, type = UnitType.INFANTRY))
        draw(units = units, players = emptyList())

        val fallbackPainter = unitPainters[PlayerColor.RED to UnitType.INFANTRY]!!
        verify {
            with(fallbackPainter) { scope.draw(any()) }
        }
    }

    // ---- Mehrere Units gleichzeitig ------------------------------------

    @Test
    fun `multiple units in different cells produce separate icons`() {
        val units = listOf(
            GameUnit(player = "Alice", x = 0, y = 0, type = UnitType.INFANTRY),
            GameUnit(player = "Bob", x = 1, y = 0, type = UnitType.CAVALRY),
            GameUnit(player = "Alice", x = 0, y = 1, type = UnitType.ARCHER)
        )
        draw(units = units, players = listOf(alice, bob))

        verify(exactly = 4) {
            scope.drawPath(any(), any<Color>(), any(), any(), any(), any())
        }
        
        verify(exactly = 1) {
            with(unitPainters[PlayerColor.RED to UnitType.INFANTRY]!!) { scope.draw(any()) }
            with(unitPainters[PlayerColor.BLUE to UnitType.CAVALRY]!!) { scope.draw(any()) }
            with(unitPainters[PlayerColor.RED to UnitType.ARCHER]!!) { scope.draw(any()) }
        }
    }

    @Test
    fun `unit on same cell overrides previous (associateBy collapses duplicates)`() {
        val units = listOf(
            GameUnit(player = "Alice", x = 0, y = 0, type = UnitType.INFANTRY),
            GameUnit(player = "Bob", x = 0, y = 0, type = UnitType.CAVALRY)
        )
        draw(units = units, players = listOf(alice, bob))

        // Nur 1 Icon (Bob ueberschreibt Alice)
        verify(exactly = 1) {
            with(unitPainters[PlayerColor.BLUE to UnitType.CAVALRY]!!) { scope.draw(any()) }
        }
        verify(exactly = 0) {
            with(unitPainters[PlayerColor.RED to UnitType.INFANTRY]!!) { scope.draw(any()) }
        }
    }
}
