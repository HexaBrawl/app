package at.aau.serg.websocketbrokerdemo.grid.renderer

import androidx.compose.ui.graphics.drawscope.DrawScope
import at.aau.serg.websocketbrokerdemo.grid.layout.GridLayout
import at.aau.serg.websocketbrokerdemo.grid.model.GridModel
import at.aau.serg.websocketbrokerdemo.grid.model.UnitData
import at.aau.serg.websocketbrokerdemo.grid.shape.GridShape
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class HexRendererTest {

    @Test
    fun `renderer should call drawHex for each cell`() {
        val drawer = mockk<HexDrawer>(relaxed = true)
        val layout = mockk<GridLayout>()
        val shape = mockk<GridShape>()
        val scope = mockk<DrawScope>(relaxed = true)

        every { shape.allCells(3, 3) } returns sequenceOf(
            0 to 0,
            1 to 0,
            0 to 1
        )

        every { layout.cellCenter(any(), any()) } returns (10f to 20f)
        every { layout.cellSize } returns 50f

        val renderer = HexRenderer(drawer)

        val model = GridModel(
            width = 3,
            height = 3,
            shape = shape,
            layout = layout,
            units = emptyList()
        )

        with(scope) {
            renderer.run { render(model) }
        }

        verify(exactly = 3) { drawer.drawHex(scope, 10f, 20f, 50f) }
    }

    @Test
    fun `renderer should call drawUnit for matching unit`() {
        val drawer = mockk<HexDrawer>(relaxed = true)
        val layout = mockk<GridLayout>()
        val shape = mockk<GridShape>()
        val scope = mockk<DrawScope>(relaxed = true)

        every { shape.allCells(1, 1) } returns sequenceOf(0 to 0)
        every { layout.cellCenter(0, 0) } returns (10f to 20f)
        every { layout.cellSize } returns 50f

        val renderer = HexRenderer(drawer)

        val model = GridModel(
            width = 1,
            height = 1,
            shape = shape,
            layout = layout,
            units = listOf(UnitData(0, 0, "P1"))
        )

        with(scope) {
            renderer.run { render(model) }
        }

        verify { drawer.drawUnit(scope, "P1", 10f, 20f, 50f) }
    }
}