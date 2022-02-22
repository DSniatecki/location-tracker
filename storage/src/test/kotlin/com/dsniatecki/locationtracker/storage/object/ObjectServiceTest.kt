package com.dsniatecki.locationtracker.storage.`object`

import com.dsniatecki.locationtracker.storage.StorageApplication
import com.dsniatecki.locationtracker.storage.cleanDb
import com.dsniatecki.locationtracker.storage.createDbTestContainer
import com.dsniatecki.locationtracker.storage.registerDbProperties
import com.dsniatecki.locationtracker.storage.toList
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(classes = [StorageApplication::class])
@Testcontainers
internal class ObjectServiceTest(
    @Autowired private val objectService: ObjectService,
    @Autowired private val databaseClient: DatabaseClient
) {
    companion object {
        @Container
        private val dbContainer = createDbTestContainer()

        @DynamicPropertySource
        @JvmStatic
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registerDbProperties(dbContainer, registry)
        }
    }

    @AfterEach
    fun clean() {
        cleanDb(databaseClient)
    }

    @Test
    fun `Should not get object when db is empty`() {
        assertThat(objectService.get(generateId()).block()).isNull()
    }

    @Test
    fun `Should not get object when object with given id doesn't exist`() {
        objectService.save(createTestObjectData("Truck1")).block()!!
        objectService.save(createTestObjectData("Truck2")).block()!!
        objectService.save(createTestObjectData("Truck3")).block()!!
        assertThat(objectService.get(generateId()).block()).isNull()
    }

    @Test
    fun `Should get saved object`() {
        val savedNewObject = objectService.save(createTestObjectData()).block()!!
        assertThat(objectService.get(savedNewObject.id).block()).isEqualTo(savedNewObject)
    }

    @Test
    fun `Should get all saved objects`() {
        val savedNewObject1 = objectService.save(createTestObjectData("Truck1")).block()!!
        val savedNewObject2 = objectService.save(createTestObjectData("Truck2")).block()!!
        assertThat(objectService.getAll().toList()).isEqualTo(listOf(savedNewObject1, savedNewObject2))
    }

    @Test
    fun `Should save object`() {
        val newObjectData = createTestObjectData()
        val savedNewObject = objectService.save(newObjectData).block()!!
        assertAll(
            { assertThat(savedNewObject.name).isEqualTo(newObjectData.name) },
            { assertThat(savedNewObject.imageUrl).isEqualTo(newObjectData.imageUrl) },
            { assertThat(savedNewObject.updatedAt).isNull() },
        )
    }

    @Test
    fun `Should update object`() {
        val savedNewObject = objectService.save(createTestObjectData("Truck1")).block()!!
        val updateData = ObjectData("UpdatedTruck1", "https://newimageurl.com")
        val updatedObject = objectService.update(savedNewObject.id, updateData).block()!!
        assertThat(updatedObject).isEqualTo(savedNewObject.copy(
            name = updateData.name, imageUrl = updateData.imageUrl, updatedAt = updatedObject.updatedAt
        ))
        assertThat(updatedObject.updatedAt).isNotNull
    }

    @Test
    fun `Should delete object`() {
        val savedNewObject = objectService.save(createTestObjectData("Truck1")).block()!!
        objectService.delete(savedNewObject.id).block()
        assertThat(objectService.get(savedNewObject.id).block()).isNull()
        assertThat(objectService.getAll().toList()).isEmpty()
    }

    private fun createTestObjectData(
        name: String = "SuperCar",
        imageUrl: String? = "https://locationtracker.images.com/${generateId()}"
    ): ObjectData =
        ObjectData(name, imageUrl)
}