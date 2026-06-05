package at.aau.serg.websocketbrokerdemo.grid

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import at.aau.serg.websocketbrokerdemo.data.serverside.Building
import at.aau.serg.websocketbrokerdemo.data.serverside.BuildingType
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
        players: List<Player> = emptyList()
    ) {
        with(renderer) {
            scope.render(tinyLayout, units, buildings, players, unitPainters, buildingPainters)
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
