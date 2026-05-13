package at.aau.serg.websocketbrokerdemo.grid.model

import at.aau.serg.websocketbrokerdemo.grid.layout.GridLayout
import at.aau.serg.websocketbrokerdemo.grid.shape.GridShape
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class GridModelTest {

    @Test
    fun `should correctly store width height shape layout and units`() {
        val shape = mockk<GridShape>()
        val layout = mockk<GridLayout>()
        val units = listOf(
            UnitData(1, 1, "P1"),
            UnitData(2, 2, "P2")
        )

        val model = GridModel(
            width = 10,
            height = 12,
            shape = shape,
            layout = layout,
            units = units
        )

        Assertions.assertEquals(10, model.width)
        Assertions.assertEquals(12, model.height)
        Assertions.assertSame(shape, model.shape)
        Assertions.assertSame(layout, model.layout)
        Assertions.assertEquals(units, model.units)
    }

    @Test
    fun `two GridModels with same values should be equal`() {
        val shape = mockk<GridShape>()
        val layout = mockk<GridLayout>()
        val units = listOf(UnitData(1, 1, "P1"))

        val a = GridModel(10, 10, shape, layout, units)
        val b = GridModel(10, 10, shape, layout, units)

        Assertions.assertEquals(a, b)
        Assertions.assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `two GridModels with different values should not be equal`() {
        val shape = mockk<GridShape>()
        val layout = mockk<GridLayout>()

        val a = GridModel(10, 10, shape, layout, emptyList())
        val b = GridModel(20, 20, shape, layout, emptyList())

        Assertions.assertNotEquals(a, b)
    }
}