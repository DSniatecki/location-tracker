package com.dsniatecki.locationtracker.archiver.objectlocation

import com.dsniatecki.locationtracker.archiver.ArchiverApplication
import com.dsniatecki.locationtracker.archiver.OBJECT_LOCATION_REQUEST_QUEUE
import com.dsniatecki.locationtracker.archiver.OBJECT_LOCATION_REQUEST_ROUTING_KEY
import com.dsniatecki.locationtracker.archiver.OBJECT_LOCATION_SOURCE_QUEUE
import com.dsniatecki.locationtracker.archiver.RABBITMQ_EXCHANGE
import com.dsniatecki.locationtracker.archiver.RABBITMQ_PASSWORD
import com.dsniatecki.locationtracker.archiver.RABBITMQ_USERNAME
import com.dsniatecki.locationtracker.archiver.RABBITMQ_VIRTUAL_HOST
import com.dsniatecki.locationtracker.archiver.cleanDb
import com.dsniatecki.locationtracker.archiver.cleanQueues
import com.dsniatecki.locationtracker.archiver.countDeadLetters
import com.dsniatecki.locationtracker.archiver.createDbTestContainer
import com.dsniatecki.locationtracker.archiver.createRabbitMqTestContainer
import com.dsniatecki.locationtracker.archiver.createTestObjectLocation
import com.dsniatecki.locationtracker.archiver.invalidMsg
import com.dsniatecki.locationtracker.archiver.message.ObjectLocationProto
import com.dsniatecki.locationtracker.archiver.message.utils.toProtoDateTime
import com.dsniatecki.locationtracker.archiver.registerDbProperties
import com.dsniatecki.locationtracker.archiver.registerRabbitMqProperties
import com.dsniatecki.locationtracker.archiver.testTime
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(
    properties = [
        "spring.rabbitmq.username=$RABBITMQ_USERNAME",
        "spring.rabbitmq.password=$RABBITMQ_PASSWORD",
        "spring.rabbitmq.virtual-host=$RABBITMQ_VIRTUAL_HOST",
        "spring.rabbitmq.object-location.source.queue=$OBJECT_LOCATION_SOURCE_QUEUE",
        "spring.rabbitmq.object-location.request.queue=$OBJECT_LOCATION_REQUEST_QUEUE",
        "spring.rabbitmq.ssl.enabled=false"
    ],
    classes = [ArchiverApplication::class]
)
@Testcontainers
internal class ObjectLocationRequestListenerTest(
    @Autowired private val objectLocationService: ObjectLocationService,
    @Autowired private val databaseClient: DatabaseClient,
    @Autowired private val rabbitTemplate: RabbitTemplate
) {

    companion object {

        private const val object1Id = "ae289617-8c98-4321-89a3-58b69cda81c0"
        private const val object2Id = "b78e6dbd-9641-4c3a-bd91-aed3c1b776bc"
        private const val object3Id = "c7862s12-2ga1-cgc3-6292-ghd6c1b12345"

        private val testTolerance = Duration.ofMinutes(5)

        @Container
        private val dbContainer = createDbTestContainer()

        @Container
        private val rabbitMqContainer = createRabbitMqTestContainer()

        @DynamicPropertySource
        @JvmStatic
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registerDbProperties(dbContainer, registry)
            registerRabbitMqProperties(rabbitMqContainer, registry)
        }
    }

    @AfterEach
    fun clean() {
        cleanDb(databaseClient)
        cleanQueues(rabbitTemplate)
    }

    @Test
    fun `Should return object1 location`() {
        val objectLocations = listOf(createTestObjectLocation(object1Id), createTestObjectLocation(object2Id))
        val objectLocationRequests = ObjectLocationProto.ObjectLocationRequests.newBuilder()
            .addRequests(createObjectLocationRequestProto(object1Id))
            .build()
        objectLocationService.saveAll(objectLocations).block()
        val response = sendAndReceiveObjectLocation(objectLocationRequests)
        val objectLocationList = response.objectLocations.objectLocationsList
        assertAll(
            { assertThat(objectLocationList.size).isEqualTo(1) },
            { assertThat(objectLocationList[0].toObjectLocation()).isEqualTo(createTestObjectLocation(object1Id)) }
        )
        assertThat(countDeadLetters()).isEqualTo(0)
    }

    @Test
    fun `Should return object locations`() {
        val objectLocations = listOf(createTestObjectLocation(object1Id), createTestObjectLocation(object2Id))
        val objectLocationRequests = ObjectLocationProto.ObjectLocationRequests.newBuilder()
            .addRequests(createObjectLocationRequestProto(object1Id))
            .addRequests(createObjectLocationRequestProto(object2Id))
            .addRequests(createObjectLocationRequestProto(object3Id))
            .build()
        objectLocationService.saveAll(objectLocations).block()
        val response = sendAndReceiveObjectLocation(objectLocationRequests)
        val objectLocationList = response.objectLocations.objectLocationsList
        assertAll(
            { assertThat(objectLocationList.size).isEqualTo(2) },
            { assertThat(objectLocationList[0].toObjectLocation()).isEqualTo(createTestObjectLocation(object1Id)) },
            { assertThat(objectLocationList[1].toObjectLocation()).isEqualTo(createTestObjectLocation(object2Id)) }
        )
        assertThat(countDeadLetters()).isEqualTo(0)
    }

    @Test
    fun `Should not return object locations when db is empty`() {
        val objectLocationRequests = ObjectLocationProto.ObjectLocationRequests.newBuilder()
            .addRequests(createObjectLocationRequestProto(object1Id))
            .build()
        val response = sendAndReceiveObjectLocation(objectLocationRequests)
        assertThat(response.objectLocations.objectLocationsList).isEmpty()
        assertThat(countDeadLetters()).isEqualTo(0)
    }

    @Test
    fun `Should return error response when requests msg is corrupted`() {
        val objectLocations = listOf(createTestObjectLocation(object1Id), createTestObjectLocation(object2Id))
        objectLocationService.saveAll(objectLocations).block()
        val response = sendAndReceiveObjectLocation(invalidMsg)
        assertAll(
            { assertThat(response.errorResponse).isNotNull },
            { assertThat(response.errorResponse.errorMessage).isNotEmpty }
        )
        assertThat(countDeadLetters()).isEqualTo(0)
    }

    fun createObjectLocationRequestProto(
        objectId: String,
        effectiveAt: LocalDateTime = testTime,
        tolerance: Duration = testTolerance
    ): ObjectLocationProto.ObjectLocationRequest =
        ObjectLocationProto.ObjectLocationRequest.newBuilder()
            .setObjectId(objectId)
            .setEffectiveAt(effectiveAt.toProtoDateTime())
            .setTolerance(testTolerance.toSeconds())
            .build()

    fun sendAndReceiveObjectLocation(
        objectLocationRequests: ObjectLocationProto.ObjectLocationRequests
    ): ObjectLocationProto.ObjectLocationRequestsResponse =
        sendAndReceiveObjectLocation(Message(objectLocationRequests.toByteArray()))

    fun sendAndReceiveObjectLocation(message: Message): ObjectLocationProto.ObjectLocationRequestsResponse {
        val properties = MessageProperties()
        properties.correlationId = UUID.randomUUID().toString()
        val msg = Message(message.body, properties)
        val responseMsg = rabbitTemplate.sendAndReceive(RABBITMQ_EXCHANGE, OBJECT_LOCATION_REQUEST_ROUTING_KEY, msg)
        return ObjectLocationProto.ObjectLocationRequestsResponse.newBuilder().mergeFrom(responseMsg!!.body).build()
    }

    private fun countDeadLetters(): Int = countDeadLetters(rabbitTemplate)
}