package at.aau.serg.websocketbrokerdemo.data.serverside

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class FieldTest {

    @Test
    fun `Field default values are correct`() {
        val field = Field()
        assertEquals(0, field.x)
        assertEquals(0, field.y)
        assertNull(field.owner)
    }

    @Test
    fun `Field correctly stores values`() {
        val field = Field(x = 3, y = 7, owner = "Alice")
        assertEquals(3, field.x)
        assertEquals(7, field.y)
        assertEquals("Alice", field.owner)
    }

    @Test
    fun `Field owner is null when neutral`() {
        val field = Field(x = 5, y = 5)
        assertNull(field.owner)
    }

    @Test
    fun `Field owner can be reassigned`() {
        val field = Field(x = 1, y = 1, owner = "Alice")
        field.owner = "Bob"
        assertEquals("Bob", field.owner)
    }

    @Test
    fun `Field owner can be set to null`() {
        val field = Field(x = 2, y = 2, owner = "Alice")
        field.owner = null
        assertNull(field.owner)
    }

    @Test
    fun `Field equality is based on all values`() {
        val a = Field(x = 1, y = 2, owner = "Alice")
        val b = Field(x = 1, y = 2, owner = "Alice")
        val c = Field(x = 1, y = 2, owner = "Bob")
        assertEquals(a, b)
        assertNotEquals(a, c)
    }

    @Test
    fun `Field copy preserves untouched values`() {
        val original = Field(x = 4, y = 6, owner = "Alice")
        val copy = original.copy(owner = "Bob")
        assertEquals(4, copy.x)
        assertEquals(6, copy.y)
        assertEquals("Bob", copy.owner)
    }
}
