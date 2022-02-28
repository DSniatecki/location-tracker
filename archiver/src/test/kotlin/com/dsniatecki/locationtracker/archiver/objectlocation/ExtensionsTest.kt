package com.dsniatecki.locationtracker.archiver.objectlocation

import java.math.BigDecimal
import java.time.OffsetDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class ExtensionsTest {

    private val testTime = OffsetDateTime.now()

    @Test
    fun `Should pass validation`() {
        val testObjectLocation = ObjectLocation(
            objectId = "c039e9fc-8b46-11ec-a8a3-0242ac120002",
            receivedAt = testTime,
            latitude = BigDecimal("24.12124212"),
            longitude = BigDecimal("64.42127643")
        )
        assertThat(testObjectLocation.validate()).isEqualTo(testObjectLocation)
    }

    @Test
    fun `Should pass validation and set proper coordinates scale`() {
        val testObjectLocation1 = ObjectLocation(
            objectId = "c039e9fc-8b46-11ec-a8a3-0242ac120002",
            receivedAt = testTime,
            latitude = BigDecimal("24.121242"),
            longitude = BigDecimal("64.421276")
        )
        val testObjectLocation2 = ObjectLocation(
            objectId = "c039e9fc-8b46-11ec-a8a3-0242ac120002",
            receivedAt = testTime,
            latitude = BigDecimal("24.1212421299"),
            longitude = BigDecimal("64.4212764399")
        )
        assertAll(
            {
                assertThat(testObjectLocation1.validate())
                    .isEqualTo(testObjectLocation1.copy(latitude = BigDecimal("24.12124200"), longitude = BigDecimal("64.42127600")))
            },
            {
                assertThat(testObjectLocation2.validate())
                    .isEqualTo(testObjectLocation2.copy(latitude = BigDecimal("24.12124213"), longitude = BigDecimal("64.42127644")))
            }
        )
    }

    @Test
    fun `Should not pass validation due to invalid object id`() {
        val testObjectLocation = ObjectLocation(
            objectId = "c039e9fc-8b46-11ec-a8a3-0242",
            receivedAt = testTime,
            latitude = BigDecimal("24.12124212"),
            longitude = BigDecimal("64.42127643")
        )
        assertThrows(IllegalStateException::class.java) { testObjectLocation.validate() }
    }

    @Test
    fun `Should not pass validation due to invalid latitude`() {
        val testObjectLocation1 = ObjectLocation(
            objectId = "c039e9fc-8b46-11ec-a8a3-0242",
            receivedAt = testTime,
            latitude = BigDecimal("-91.12124212"),
            longitude = BigDecimal("64.42127643")
        )
        val testObjectLocation2 = ObjectLocation(
            objectId = "c039e9fc-8b46-11ec-a8a3-0242",
            receivedAt = testTime,
            latitude = BigDecimal("91.12124212"),
            longitude = BigDecimal("64.42127643")
        )
        assertAll(
            { assertThrows(IllegalStateException::class.java) { testObjectLocation1.validate() } },
            { assertThrows(IllegalStateException::class.java) { testObjectLocation2.validate() } }
        )
    }

    @Test
    fun `Should not pass validation due to invalid longitude`() {
        val testObjectLocation1 = ObjectLocation(
            objectId = "c039e9fc-8b46-11ec-a8a3-0242",
            receivedAt = testTime,
            latitude = BigDecimal("24.12124212"),
            longitude = BigDecimal("-180.42127643")
        )
        val testObjectLocation2 = ObjectLocation(
            objectId = "c039e9fc-8b46-11ec-a8a3-0242",
            receivedAt = testTime,
            latitude = BigDecimal("24.12124212"),
            longitude = BigDecimal("180.42127643")
        )
        assertAll(
            { assertThrows(IllegalStateException::class.java) { testObjectLocation1.validate() } },
            { assertThrows(IllegalStateException::class.java) { testObjectLocation2.validate() } }
        )
    }
}