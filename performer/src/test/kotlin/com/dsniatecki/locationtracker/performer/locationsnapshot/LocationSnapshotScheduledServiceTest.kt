package com.dsniatecki.locationtracker.performer.locationsnapshot

import com.dsniatecki.locationtracker.archiver.api.ObjectLocationControllerApi
import com.dsniatecki.locationtracker.archiver.model.ObjectLocation
import com.dsniatecki.locationtracker.commons.utils.TimeSupplier
import com.dsniatecki.locationtracker.performer.config.props.LocationSnapshotJobProps
import com.dsniatecki.locationtracker.performer.sftp.SftpDestination
import com.dsniatecki.locationtracker.storage.api.ObjectControllerApi
import com.dsniatecki.locationtracker.storage.model.ModelObject
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.UUID
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Flux

@ExtendWith(MockKExtension::class)
internal class LocationSnapshotScheduledServiceTest {

    private val testTime = LocalDateTime.now()

    private val testTimeSupplier = object : TimeSupplier {
        override fun now() = testTime
        override fun zoneId(): ZoneId = ZoneId.of("UTC")
        override fun zoneOffset(): ZoneOffset = ZoneOffset.UTC
    }

    @MockK
    lateinit var objectControllerApi: ObjectControllerApi

    @MockK
    lateinit var objectLocationControllerApi: ObjectLocationControllerApi

    @MockK
    lateinit var locationSnapshotSender: LocationSnapshotSender

    @Test
    fun `Should successfully execute location snapshot job`() {
        val testModelObject = ModelObject()
            .id(generateId())
            .name("Test object")
            .createdAt(testTime.atOffset(ZoneOffset.UTC))

        val testObjectLocation = ObjectLocation()
            .objectId(testModelObject.id)
            .receivedAt(testTime.atOffset(ZoneOffset.UTC))
            .latitude(BigDecimal.ONE)
            .longitude(BigDecimal.ONE)

        val expectedLocationSnapshot = LocationSnapshot(
            objectId = testModelObject.id,
            objectName = testModelObject.name,
            latitude = testObjectLocation.latitude,
            longitude = testObjectLocation.longitude,
            effectiveAt = testTime,
            receivedAt = testTime
        )
        val scheduledService = LocationSnapshotScheduledService(
            objectController = objectControllerApi,
            objectLocationController = objectLocationControllerApi,
            locationSnapshotSender = locationSnapshotSender,
            timeSupplier = testTimeSupplier,
            props = LocationSnapshotJobProps(
                schedulerCron = "1 * * * * *",
                tolerance = Duration.ofSeconds(60),
                sftp = SftpDestination("test", 22, "test", "test", "test"),
                objectIds = setOf(testModelObject.id)
            )
        )
        every { objectControllerApi.getObjects(any()) } returns Flux.just(testModelObject)
        every { objectLocationControllerApi.getEffectiveObjectLocations(any(), any(), any()) } returns Flux.just(testObjectLocation)

        scheduledService.execute()

        verify(exactly = 1) { locationSnapshotSender.send(listOf(expectedLocationSnapshot), testTime) }
    }

    @Test
    fun `Should successfully execute location snapshot job but sent empty file due to non existing object`() {
        val scheduledService = LocationSnapshotScheduledService(
            objectController = objectControllerApi,
            objectLocationController = objectLocationControllerApi,
            locationSnapshotSender = locationSnapshotSender,
            timeSupplier = testTimeSupplier,
            props = LocationSnapshotJobProps(
                schedulerCron = "1 * * * * *",
                tolerance = Duration.ofSeconds(60),
                sftp = SftpDestination("test", 22, "test", "test", "test"),
                objectIds = setOf(generateId())
            )
        )

        every { objectControllerApi.getObjects(any()) } returns Flux.just()
        every { objectLocationControllerApi.getEffectiveObjectLocations(any(), any(), any()) } returns Flux.just()

        scheduledService.execute()

        verify(exactly = 1) { locationSnapshotSender.send(listOf(), testTime) }
    }

    private fun generateId(): String = UUID.randomUUID().toString()
}