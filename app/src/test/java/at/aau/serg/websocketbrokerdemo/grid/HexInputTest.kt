package at.aau.serg.websocketbrokerdemo.grid

import at.aau.serg.websocketbrokerdemo.grid.input.HexInput
import at.aau.serg.websocketbrokerdemo.grid.layout.GridLayout
import at.aau.serg.websocketbrokerdemo.grid.model.GridModel
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class HexInputTest {

    @Test
    fun `detect should return cell from layout when hit`() {
        // Given
        val layout = mockk<GridLayout>()
        val model = mockk<GridModel>()

        every { model.layout } returns layout
        every { layout.pixelToCell(100f, 200f) } returns (3 to 4)

        // When
        val result = HexInput.detect(100f, 200f, model)

        // Then
        assertEquals(3 to 4, result)
    }

    @Test
    fun `detect should return null when layout returns null`() {
        // Given
        val layout = mockk<GridLayout>()
        val model = mockk<GridModel>()

        every { model.layout } returns layout
        every { layout.pixelToCell(50f, 50f) } returns null

        // When
        val result = HexInput.detect(50f, 50f, model)

        // Then
        assertNull(result)
    }
}
