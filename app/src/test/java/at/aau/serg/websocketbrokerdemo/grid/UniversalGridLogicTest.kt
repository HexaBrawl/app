package at.aau.serg.websocketbrokerdemo.grid

import at.aau.serg.websocketbrokerdemo.grid.input.GridInput
import at.aau.serg.websocketbrokerdemo.grid.model.GridModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class UniversalGridLogicTest {

    private val model = mockk<GridModel>()

    @Test
    fun `handleTap triggers onCellClicked when detect returns a cell`() {
        val input = mockk<GridInput>()
        every { input.detect(10f, 20f, model) } returns Pair(3, 5)

        val onCellClicked = mockk<(Int, Int) -> Unit>(relaxed = true)

        UniversalGridLogic.handleTap(
            x = 10f,
            y = 20f,
            model = model,
            input = input,
            onCellClicked = onCellClicked
        )

        verify { onCellClicked(3, 5) }
    }

    @Test
    fun `handleTap does nothing when detect returns null`() {
        val input = mockk<GridInput>()
        every { input.detect(any(), any(), any()) } returns null

        val onCellClicked = mockk<(Int, Int) -> Unit>(relaxed = true)

        UniversalGridLogic.handleTap(
            x = 50f,
            y = 50f,
            model = model,
            input = input,
            onCellClicked = onCellClicked
        )

        verify(exactly = 0) { onCellClicked(any(), any()) }
    }
}