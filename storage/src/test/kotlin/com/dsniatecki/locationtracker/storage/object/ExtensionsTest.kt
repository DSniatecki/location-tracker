package com.dsniatecki.locationtracker.storage.`object`

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class ExtensionsTest {

    @Test
    fun `Should pass validation`() {
        val testNewObject = NewObject("SuperCar", "https://locationtracker.images.com/${generateId()}")
        assertThat(testNewObject.validate()).isEqualTo(testNewObject)
    }

    @Test
    fun `Should pass validation without image url`() {
        val testNewObject = NewObject("SuperCar")
        assertThat(testNewObject.validate()).isEqualTo(testNewObject)
    }

    @Test
    fun `Should not pass validation due to invalid object name`() {
        val testNewObject1 = NewObject("")
        val testNewObject2 = NewObject("A".repeat(maxNameLength + 1))
        assertAll(
            { assertThrows(IllegalStateException::class.java) { testNewObject1.validate() } },
            { assertThrows(IllegalStateException::class.java) { testNewObject2.validate() } }
        )
    }

    @Test
    fun `Should not pass validation due to invalid object imageUrl`() {
        val testNewObject1 = NewObject("SuperCar", "B".repeat(minImageUrlLength - 1))
        val testNewObject2 = NewObject("SuperCar", "B".repeat(maxImageUrlLength + 1))
        assertAll(
            { assertThrows(IllegalStateException::class.java) { testNewObject1.validate() } },
            { assertThrows(IllegalStateException::class.java) { testNewObject2.validate() } }
        )
    }
}