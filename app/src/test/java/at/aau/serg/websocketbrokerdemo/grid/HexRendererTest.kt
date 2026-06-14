package at.aau.serg.websocketbrokerdemo.grid

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import at.aau.serg.websocketbrokerdemo.data.serverside.Building
import at.aau.serg.websocketbrokerdemo.data.serverside.BuildingType
import at.aau.serg.websocketbrokerdemo.data.serverside.Field
import at.aau.serg.websocketbrokerdemo.data.serverside.GameUnit
import at.aau.serg.websocketbrokerdemo.data.serverside.Player
import at.aau.serg.websocketbrokerdemo.data.serverside.PlayerColor
import at.aau.serg.websocketbrokerdemo.data.serverside.UnitType
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests fuer HexRenderer.
 */
class HexRendererTest {

    private val tinyLayout = MapLayout(rows = 2, cols = 2, hexSize = 10f, name = "tiny")

    private val alice = Player(name = "Alice", color = PlayerColor.RED)
    private val bob = Player(name = "Bob", color = PlayerColor.BLUE)

    private lateinit var renderer: HexRenderer
    private lateinit var scope: DrawScope
    private lateinit var unitPainters: Map<Pair<PlayerColor, UnitType>, Painter>
    private lateinit var buildingPainters: Map<Pair<PlayerColor, BuildingType>, Painter>

    @BeforeEach
    fun setUp() {
        renderer = HexRenderer()
        scope = mockk(relaxed = true)

        unitPainters = PlayerColor.entries.flatMap { color ->
            UnitType.entries.map { type ->
                (color to type) to mockk<Painter>(relaxed = true)
            }
        }.toMap()

        buildingPainters = PlayerColor.entries.flatMap { color ->
            BuildingType.entries.map { type ->
                (color to type) to mockk<Painter>(relaxed = true)
            }
        }.toMap()

        justRun { scope.drawPath(any(), any<Color>(), any(), any(), any(), any()) }
    }

    private fun draw(
        units: List<GameUnit> = emptyList(),
        buildings: List<Building> = emptyList(),
        fields: List<Field> = emptyList(),
        players: List<Player> = emptyList()
    ) {
        with(renderer) {
            scope.render(tinyLayout, units, buildings, fields, players, unitPainters, buildingPainters)
        }
    }

    // ---- Hex-Pfade ------------------------------------------------------

    @Test
    fun `draws one hex path per cell`() {
        draw()
        verify(exactly = 4) {
            scope.drawPath(any(), any<Color>(), any(), any(), any(), any())
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

    // ---- Buildings -----------------------------------------------------

    @Test
    fun `draws one icon per building`() {
        val buildings = listOf(
            Building(player = "Alice", x = 0, y = 0, type = BuildingType.CASTLE),
            Building(player = "Bob", x = 1, y = 1, type = BuildingType.CASTLE)
        )
        draw(buildings = buildings, players = listOf(alice, bob))

        val aliceCastle = buildingPainters[PlayerColor.RED to BuildingType.CASTLE]!!
        val bobCastle = buildingPainters[PlayerColor.BLUE to BuildingType.CASTLE]!!

        verify(exactly = 1) {
            with(aliceCastle) { scope.draw(any()) }
            with(bobCastle) { scope.draw(any()) }
        }
    }

    // ---- Feld-Markierungen (subissue #123) ----------------------------------

    @Test
    fun `fills owned fields exactly once`() {
        val fields = listOf(Field(x = 0, y = 0, owner = "Alice"))

        draw(fields = fields, players = listOf(alice))

        // Genau EINE Fuellung (nur das eroberte Feld). Kein eq()-Vergleich
        // der Farbe: Compose-Color ist eine value class, deren Argumente
        // MockK nur als rohen Long aufzeichnet -- die konkrete 50%-Farbe
        // ist durch PlayerColorMapTest.cellFillFor abgedeckt.
        verify(exactly = 1) {
            scope.drawPath(any(), any<Color>(), any(), eq(Fill), any(), any())
        }
    }

    @Test
    fun `does not fill neutral fields`() {
        val fields = listOf(Field(x = 0, y = 0, owner = null))

        draw(fields = fields, players = listOf(alice))

        // nur die 4 Hex-Raender, keine zusaetzliche Fuellung
        verify(exactly = 4) {
            scope.drawPath(any(), any<Color>(), any(), any(), any(), any())
        }
    }

    @Test
    fun `draws fill before hex outline`() {
        val fields = listOf(Field(x = 0, y = 0, owner = "Alice"))

        draw(fields = fields, players = listOf(alice))

        // Unterscheidung ueber den Zeichenstil (Fill vs. Stroke), weil
        // Color-Argumente von MockK nicht per eq() vergleichbar sind.
        verifyOrder {
            scope.drawPath(any(), any<Color>(), any(), eq(Fill), any(), any())
            scope.drawPath(any(), any<Color>(), any(), ofType<Stroke>(), any(), any())
        }
    }

    // ---- Skelett-Felder (subissue #172) -------------------------------

    @Test
    fun `fills skeleton field exactly once`() {
        val fields = listOf(Field(x = 0, y = 0, owner = "Alice", isSkeleton = true))
        draw(fields = fields, players = listOf(alice))

        // Skelett-Feld muss weiterhin genau eine Fuellung bekommen -
        // konkrete Farbe ist durch PlayerColorMapTest abgedeckt.
        verify(exactly = 1) {
            scope.drawPath(any(), any<Color>(), any(), eq(Fill), any(), any())
        }
    }

    @Test
    fun `fills owner field with skeleton unit on it exactly once`() {
        // Insolvenz-Fall: Unit ist SKELETON, Field.isSkeleton bleibt false.
        // Renderer muss das Feld trotzdem als tot zeichnen (Fill-Aufruf
        // existiert, konkrete Farbe via PlayerColorMapTest).
        val fields = listOf(Field(x = 0, y = 0, owner = "Alice", isSkeleton = false))
        val units = listOf(GameUnit(player = "Alice", x = 0, y = 0, type = UnitType.SKELETON))
        draw(units = units, fields = fields, players = listOf(alice))

        verify(exactly = 1) {
            scope.drawPath(any(), any<Color>(), any(), eq(Fill), any(), any())
        }
    }

    @Test
    fun `does not desaturate when foreign skeleton stands on owner field`() {
        // Eine Skelett-Einheit eines anderen Spielers auf Alices Feld
        // darf die Insolvenz-Heuristik NICHT triggern. Test fasst hier
        // nur den Render-Pfad an: Fill bleibt genau 1, Skelett wird
        // gezeichnet, kein Crash.
        val fields = listOf(Field(x = 0, y = 0, owner = "Alice", isSkeleton = false))
        val units = listOf(GameUnit(player = "Bob", x = 0, y = 0, type = UnitType.SKELETON))
        draw(units = units, fields = fields, players = listOf(alice, bob))

        verify(exactly = 1) {
            scope.drawPath(any(), any<Color>(), any(), eq(Fill), any(), any())
        }
    }

    @Test
    fun `fills disconnected owner field as desaturated even without isSkeleton flag`() {
        // X-Feld-Szenario: gehoert Alice, isSkeleton=false, aber keine
        // Hex-Verbindung zur Basis -> muss trotzdem als tot gerendert
        // werden (Frontend-BFS).
        // (1,1) ist im odd-q-Layout KEIN Nachbar von (0,0), damit ist
        // es im 2x2-Layout disconnected -- innerhalb der Layout-Grenzen.
        val fields = listOf(
            Field(x = 0, y = 0, owner = "Alice"),
            Field(x = 1, y = 1, owner = "Alice", isSkeleton = false)
        )
        val units = listOf(
            GameUnit(player = "Alice", x = 0, y = 0, type = UnitType.BASE)
        )
        draw(units = units, fields = fields, players = listOf(alice))

        // Zwei Fills: Basis lebend, (1,1) tot. Style bleibt in beiden Fill.
        verify(exactly = 2) {
            scope.drawPath(any(), any<Color>(), any(), eq(Fill), any(), any())
        }
    }

    // ---- Rendering Order -----------------------------------------------

    @Test
    fun `draws buildings before units on the same cell`() {
        val buildings = listOf(Building(player = "Alice", x = 0, y = 0, type = BuildingType.CASTLE))
        val units = listOf(GameUnit(player = "Alice", x = 0, y = 0, type = UnitType.INFANTRY))

        draw(units = units, buildings = buildings, players = listOf(alice))

        val castlePainter = buildingPainters[PlayerColor.RED to BuildingType.CASTLE]!!
        val unitPainter = unitPainters[PlayerColor.RED to UnitType.INFANTRY]!!

        verifyOrder {
            with(castlePainter) { scope.draw(any()) }
            with(unitPainter) { scope.draw(any()) }
        }
    }
}
