package at.aau.serg.websocketbrokerdemo.grid.renderer

import androidx.compose.ui.graphics.drawscope.DrawScope
import at.aau.serg.websocketbrokerdemo.grid.layout.GridLayout
import at.aau.serg.websocketbrokerdemo.grid.model.GridModel
import at.aau.serg.websocketbrokerdemo.grid.shape.GridShape
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ComposeHexDrawerTest {

    private lateinit var scope: DrawScope
    private lateinit var drawer: ComposeHexDrawer

    @BeforeEach
    fun setUp() {
        // relaxed: alle draw*-Aufrufe werden einfach geschluckt
        scope = mockk(relaxed = true)
        drawer = ComposeHexDrawer()
    }

    @Test
    fun `drawUnit executes without crashing`() {
        drawer.drawUnit(scope, player = "P1", cx = 100f, cy = 100f, size = 50f)
        assert(true)
    }

    @Test
    fun `drawHex is invoked`() {
        val drawer = spyk(ComposeHexDrawer())

        // Echter Code wird NICHT ausgeführt → Path wird NICHT erzeugt
        every {
            drawer.drawHex(
                scope = match { true },
                cx = match { true },
                cy = match { true },
                size = match { true }
            )
        } just Runs

        val renderer = HexRenderer(drawer)

        val layout = mockk<GridLayout>()
        val shape = mockk<GridShape>()
        val scope = mockk<DrawScope>(relaxed = true)

        every { shape.allCells(1, 1) } returns sequenceOf(0 to 0)
        every { layout.cellCenter(0, 0) } returns (10f to 20f)
        every { layout.cellSize } returns 50f

        val model = GridModel(
            width = 1,
            height = 1,
            shape = shape,
            layout = layout,
            units = emptyList()
        )

        with(scope) {
            renderer.run { render(model) }
        }

        verify(exactly = 1) {
            drawer.drawHex(
                scope = match { true },
                cx = match { true },
                cy = match { true },
                size = match { true }
            )
        }
    }



}