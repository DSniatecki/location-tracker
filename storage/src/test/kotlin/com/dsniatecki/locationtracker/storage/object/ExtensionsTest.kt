package com.dsniatecki.locationtracker.storage.`object`

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class ExtensionsTest {

    @Test
    fun `Should pass NewObject validation when id is specified`() {
        val testNewObject = NewObject(generateId(), "SuperCar", "https://locationtracker.images.com/${generateId()}")
        assertThat(testNewObject.validate()).isEqualTo(testNewObject)
    }

    @Test
    fun `Should pass NewObject validation without id and imageUrl`() {
        val testNewObject = NewObject(name = "SuperCar")
        assertThat(testNewObject.validate()).isEqualTo(testNewObject)
    }

    @Test
    fun `Should not pass NewObject validation due to invalid object id`() {
        val testNewObject1 = NewObject("0".repeat(idLength - 1), "SuperCar")
        val testNewObject2 = NewObject("0".repeat(idLength + 1), "SuperCar")
        assertAll(
            { assertThrows(IllegalStateException::class.java) { testNewObject1.validate() } },
            { assertThrows(IllegalStateException::class.java) { testNewObject2.validate() } }
        )
    }

    @Test
    fun `Should not pass NewObject validation due to invalid object name`() {
        val testNewObject1 = NewObject(generateId(), "")
        val testNewObject2 = NewObject(generateId(), "A".repeat(maxNameLength + 1))
        assertAll(
            { assertThrows(IllegalStateException::class.java) { testNewObject1.validate() } },
            { assertThrows(IllegalStateException::class.java) { testNewObject2.validate() } }
        )
    }

    @Test
    fun `Should pass ObjectUpdate validation`() {
        val testObjectUpdate = ObjectUpdate("SuperCar", "https://locationtracker.images.com/${generateId()}")
        assertThat(testObjectUpdate.validate()).isEqualTo(testObjectUpdate)
    }

    @Test
    fun `Should pass ObjectUpdate validation without image url`() {
        val testObjectUpdate = ObjectUpdate("SuperCar")
        assertThat(testObjectUpdate.validate()).isEqualTo(testObjectUpdate)
    }

    @Test
    fun `Should not pass ObjectUpdate validation due to invalid object name`() {
        val testObjectUpdate1 = ObjectUpdate("")
        val testObjectUpdate2 = ObjectUpdate("A".repeat(maxNameLength + 1))
        assertAll(
            { assertThrows(IllegalStateException::class.java) { testObjectUpdate1.validate() } },
            { assertThrows(IllegalStateException::class.java) { testObjectUpdate2.validate() } }
        )
    }

    @Test
    fun `Should not pass ObjectUpdate validation due to invalid object imageUrl`() {
        val testObjectUpdate1 = ObjectUpdate("SuperCar", "B".repeat(minImageUrlLength - 1))
        val testObjectUpdate2 = ObjectUpdate("SuperCar", "B".repeat(maxImageUrlLength + 1))
        assertAll(
            { assertThrows(IllegalStateException::class.java) { testObjectUpdate1.validate() } },
            { assertThrows(IllegalStateException::class.java) { testObjectUpdate2.validate() } }
        )
    }
}