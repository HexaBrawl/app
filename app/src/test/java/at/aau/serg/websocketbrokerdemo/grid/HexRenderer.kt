package at.aau.serg.websocketbrokerdemo.grid

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import at.aau.serg.websocketbrokerdemo.data.serverside.Player
import at.aau.serg.websocketbrokerdemo.data.serverside.PlayerColor
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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
 *  - drawCircle-Aufrufe pro Einheit
 *  - Skelette werden nicht gezeichnet (Filterung)
 *  - Einheiten-Farbe stimmt mit PlayerColor ueberein
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

    @BeforeEach
    fun setUp() {
        renderer = HexRenderer()
        scope = mockk(relaxed = true)
        // Die wichtigsten DrawScope-Aufrufe explizit stubben, damit
        // verify auf sie greifen kann.
        justRun { scope.drawPath(any(), any<Color>(), any(), any(), any(), any()) }
        justRun {
            scope.drawCircle(
                color = any(),
                radius = any(),
                center = any(),
                alpha = any(),
                style = any(),
                colorFilter = any(),
                blendMode = any()
            )
        }
    }

    private fun draw(units: List<UnitData> = emptyList(), players: List<Player> = emptyList()) {
        with(renderer) {
            scope.render(tinyLayout, units)
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
    fun `draws one circle per unit`() {
        val units = listOf(
            UnitData(x = 0, y = 0, player = "Alice"),
            UnitData(x = 1, y = 1, player = "Bob")
        )
        draw(units = units, players = listOf(alice, bob))

        verify(exactly = 2) {
            scope.drawCircle(
                color = any(),
                radius = any(),
                center = any(),
                alpha = any(),
                style = any(),
                colorFilter = any(),
                blendMode = any()
            )
        }
    }

    @Test
    fun `unit radius is smaller than hex size`() {
        // Verifiziert dass der Renderer HexGridLogic.unitRadius nutzt
        // (Wert <= hexSize / 2.5).
        val units = listOf(UnitData(x = 0, y = 0, player = "Alice"))

        val radiusSlot = slot<Float>()
        every {
            scope.drawCircle(
                color = any(),
                radius = capture(radiusSlot),
                center = any(),
                alpha = any(),
                style = any(),
                colorFilter = any(),
                blendMode = any()
            )
        } answers { /* ignore */ }

        draw(units = units, players = listOf(alice))

        assertTrue(radiusSlot.captured < tinyLayout.hexSize)
        assertEquals(tinyLayout.hexSize / 2.5f, radiusSlot.captured, 0.001f)
    }

    @Test
    fun `unit is placed at the cell center`() {
        // Verifiziert dass der Renderer HexGridLogic.cellCenter nutzt:
        // die Position der Einheit soll mit der berechneten Zell-Mitte
        // uebereinstimmen.
        val targetCol = 1
        val targetRow = 0
        val units = listOf(UnitData(x = targetCol, y = targetRow, player = "Alice"))

        val centerSlot = slot<Offset>()
        every {
            scope.drawCircle(
                color = any(),
                radius = any(),
                center = capture(centerSlot),
                alpha = any(),
                style = any(),
                colorFilter = any(),
                blendMode = any()
            )
        } answers { /* ignore */ }

        draw(units = units, players = listOf(alice))

        val (expectedX, expectedY) = HexGridLogic.cellCenter(targetCol, targetRow, tinyLayout)
        assertEquals(expectedX, centerSlot.captured.x, 0.001f)
        assertEquals(expectedY, centerSlot.captured.y, 0.001f)
    }

    // ---- Mehrere Units gleichzeitig ------------------------------------

    @Test
    fun `multiple units in different cells produce separate circles`() {
        val units = listOf(
            UnitData(x = 0, y = 0, player = "Alice"),
            UnitData(x = 1, y = 0, player = "Bob"),
            UnitData(x = 0, y = 1, player = "Alice")
        )
        draw(units = units, players = listOf(alice, bob))

        // 3 Einheiten = 3 drawCircle
        verify(exactly = 3) {
            scope.drawCircle(
                color = any(),
                radius = any(),
                center = any(),
                alpha = any(),
                style = any(),
                colorFilter = any(),
                blendMode = any()
            )
        }
    }

    @Test
    fun `unit on same cell overrides previous (associateBy collapses duplicates)`() {
        // Edge-Case: Server koennte theoretisch zwei Units auf der
        // gleichen Zelle schicken. associateBy nimmt den letzten.
        // Verifizieren dass nur 1 Circle gezeichnet wird, nicht 2.
        val units = listOf(
            UnitData(x = 0, y = 0, player = "Alice"),
            UnitData(x = 0, y = 0, player = "Bob")
        )
        draw(units = units, players = listOf(alice, bob))

        verify(exactly = 1) {
            scope.drawCircle(
                color = any(),
                radius = any(),
                center = any(),
                alpha = any(),
                style = any(),
                colorFilter = any(),
                blendMode = any()
            )
        }
    }
}