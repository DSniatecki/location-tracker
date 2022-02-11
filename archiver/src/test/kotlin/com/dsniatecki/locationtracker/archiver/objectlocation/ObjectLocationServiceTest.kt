package com.dsniatecki.locationtracker.archiver.objectlocation

import com.dsniatecki.locationtracker.archiver.ArchiverApplication
import com.dsniatecki.locationtracker.archiver.cleanDb
import com.dsniatecki.locationtracker.archiver.createDbTestContainer
import com.dsniatecki.locationtracker.archiver.createTestObjectLocation
import com.dsniatecki.locationtracker.archiver.generateId
import com.dsniatecki.locationtracker.archiver.registerDbProperties
import com.dsniatecki.locationtracker.archiver.testTime
import java.math.BigDecimal
import java.time.Duration
import java.time.ZoneOffset
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(classes = [ArchiverApplication::class])
@Testcontainers
internal class ObjectLocationServiceTest(
    @Autowired private val objectLocationService: ObjectLocationService,
    @Autowired private val databaseClient: DatabaseClient
) {
    companion object {

        private val object1Id = generateId()
        private val object2Id = generateId()
        private val testTolerance = Duration.ofMinutes(5)

        @Container
        private val dbContainer = createDbTestContainer()

        @DynamicPropertySource
        @JvmStatic
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registerDbProperties(registry, dbContainer)
        }
    }

    @AfterEach
    fun clean() {
        cleanDb(databaseClient)
    }

    @Test
    fun `Should get no object location when db is empty`() {
        assertThat(objectLocationService.getEffectiveAt("OBJECT_ID", testTime, testTolerance).block()).isNull()
    }

    @Test
    fun `Should get no object location for given object id`() {
        objectLocationService.saveAll(createTestObjectLocations()).block()
        assertThat(objectLocationService.getEffectiveAt("OBJECT_ID", testTime, testTolerance).block()).isNull()
    }

    @Test
    fun `Should get no object location due to too short tolerance`() {
        val tolerance = Duration.ofSeconds(3)
        objectLocationService.saveAll(createTestObjectLocations()).block()
        val returnedObjectLocation =
            objectLocationService.getEffectiveAt(object1Id, testTime.minusSeconds(1), tolerance).block()
        assertThat(returnedObjectLocation).isNull()
    }

    @Test
    fun `Should get effective object location when it is in tolerance`() {
        objectLocationService.saveAll(createTestObjectLocations()).block()
        val time = testTime.minusSeconds(4)
        val returnedObjectLocation = objectLocationService.getEffectiveAt(object1Id, time, testTolerance).block()
        assertThat(returnedObjectLocation).isEqualTo(createTestObjectLocation(object1Id, testTime.minusSeconds(5)))
    }

    @Test
    fun `Should get effective object location for past time`() {
        objectLocationService.saveAll(createTestObjectLocations()).block()
        val time = testTime.minusSeconds(4)
        val returnedObjectLocation = objectLocationService.getEffectiveAt(object1Id, time, testTolerance).block()
        assertThat(returnedObjectLocation).isEqualTo(createTestObjectLocation(object1Id, testTime.minusSeconds(5)))
    }

    @Test
    fun `Should get effective object location for future time`() {
        objectLocationService.saveAll(createTestObjectLocations()).block()
        val time = testTime.plusSeconds(6)
        val returnedObjectLocation = objectLocationService.getEffectiveAt(object1Id, time).block()
        assertThat(returnedObjectLocation).isEqualTo(createTestObjectLocation(object1Id, testTime.plusSeconds(5)))
    }

    @Test
    fun `Should get effective object location with OffsetDateTime +2 UTC`() {
        objectLocationService.saveAll(createTestObjectLocations()).block()
        val time = testTime.plusHours(2).atOffset(ZoneOffset.ofHours(2))
        val returnedObjectLocation = objectLocationService.getEffectiveAt(object1Id, time).block()
        assertThat(returnedObjectLocation).isEqualTo(createTestObjectLocation(object1Id, testTime))
    }

    @Test
    fun `Should get object location with biggest id when both have the same received at value`() {
        val expectedObjectLocation = createTestObjectLocation(
            objectId = object1Id,
            receivedAt = testTime,
            latitude = BigDecimal("0.00000000"),
            longitude = BigDecimal("0.00000000")
        )
        objectLocationService.saveAll(createTestObjectLocations()).block()
        objectLocationService.saveAll(listOf(expectedObjectLocation)).block()
        val returnedObjectLocation = objectLocationService.getEffectiveAt(object1Id, testTime).block()
        assertThat(returnedObjectLocation).isEqualTo(expectedObjectLocation)
    }

    private fun createTestObjectLocations(): Iterable<ObjectLocation> =
        listOf(
            createTestObjectLocation(object1Id, testTime.minusSeconds(5)),
            createTestObjectLocation(object2Id, testTime.minusSeconds(5)),
            createTestObjectLocation(object1Id),
            createTestObjectLocation(object2Id),
            createTestObjectLocation(object1Id, testTime.plusSeconds(5)),
            createTestObjectLocation(object2Id, testTime.plusSeconds(5))
        )
}