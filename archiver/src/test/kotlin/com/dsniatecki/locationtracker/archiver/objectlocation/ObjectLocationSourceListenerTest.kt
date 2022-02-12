package com.dsniatecki.locationtracker.archiver.objectlocation

import com.dsniatecki.locationtracker.archiver.ArchiverApplication
import com.dsniatecki.locationtracker.archiver.OBJECT_LOCATION_REQUEST_QUEUE
import com.dsniatecki.locationtracker.archiver.OBJECT_LOCATION_SOURCE_QUEUE
import com.dsniatecki.locationtracker.archiver.OBJECT_LOCATION_SOURCE_ROUTING_KEY
import com.dsniatecki.locationtracker.archiver.RABBITMQ_EXCHANGE
import com.dsniatecki.locationtracker.archiver.RABBITMQ_PASSWORD
import com.dsniatecki.locationtracker.archiver.RABBITMQ_USERNAME
import com.dsniatecki.locationtracker.archiver.RABBITMQ_VIRTUAL_HOST
import com.dsniatecki.locationtracker.archiver.TIMEOUT_VALUE_MILLIS
import com.dsniatecki.locationtracker.archiver.cleanDb
import com.dsniatecki.locationtracker.archiver.cleanQueues
import com.dsniatecki.locationtracker.archiver.countDeadLetters
import com.dsniatecki.locationtracker.archiver.countDeadLettersRetrying
import com.dsniatecki.locationtracker.archiver.createDbTestContainer
import com.dsniatecki.locationtracker.archiver.createRabbitMqTestContainer
import com.dsniatecki.locationtracker.archiver.createTestObjectLocation
import com.dsniatecki.locationtracker.archiver.generateId
import com.dsniatecki.locationtracker.archiver.invalidMsg
import com.dsniatecki.locationtracker.archiver.message.ObjectLocationProto
import com.dsniatecki.locationtracker.archiver.registerDbProperties
import com.dsniatecki.locationtracker.archiver.registerRabbitMqProperties
import com.dsniatecki.locationtracker.archiver.testTime
import com.dsniatecki.locationtracker.archiver.waitUntilDbIsNotEmpty
import java.util.concurrent.TimeUnit
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.springframework.amqp.core.Message
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
internal class ObjectLocationSourceListenerTest(
    @Autowired private val objectLocationService: ObjectLocationService,
    @Autowired private val databaseClient: DatabaseClient,
    @Autowired private val rabbitTemplate: RabbitTemplate
) {

    companion object {

        private val object1Id = generateId()
        private val object2Id = generateId()

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
    @Timeout(value = TIMEOUT_VALUE_MILLIS, unit = TimeUnit.MILLISECONDS)
    fun `Should save object locations from messages`() {
        sendViaRabbitMq(createTestObjectLocation(object1Id))
        sendViaRabbitMq(createTestObjectLocation(object2Id))
        waitUntilDbIsNotEmpty(databaseClient)
        val time = testTime.plusSeconds(5)
        val object1Location = objectLocationService.getEffectiveAt(object1Id, time).block()!!
        val object2Location = objectLocationService.getEffectiveAt(object2Id, time).block()!!
        assertThat(object1Location).isEqualTo(createTestObjectLocation(object1Id))
        assertThat(object2Location).isEqualTo(createTestObjectLocation(object2Id))
        assertThat(countDeadLetters()).isEqualTo(0)
    }

    @Test
    @Timeout(value = TIMEOUT_VALUE_MILLIS, unit = TimeUnit.MILLISECONDS)
    fun `Should send corrupted message to dead letter queue`() {
        sendViaRabbitMq(invalidMsg)
        assertThat(countDeadLettersRetrying()).isEqualTo(1)
    }

    @Test
    @Timeout(value = TIMEOUT_VALUE_MILLIS, unit = TimeUnit.MILLISECONDS)
    fun `Should save object locations when some messages are corrupted`() {
        sendViaRabbitMq(invalidMsg)
        sendViaRabbitMq(createTestObjectLocation(object1Id))
        sendViaRabbitMq(invalidMsg)
        sendViaRabbitMq(createTestObjectLocation(object2Id))
        sendViaRabbitMq(invalidMsg)
        waitUntilDbIsNotEmpty(databaseClient)
        val time = testTime.plusSeconds(5)
        val object1Location = objectLocationService.getEffectiveAt(object1Id, time).block()!!
        val object2Location = objectLocationService.getEffectiveAt(object2Id, time).block()!!
        assertThat(object1Location).isEqualTo(createTestObjectLocation(object1Id))
        assertThat(object2Location).isEqualTo(createTestObjectLocation(object2Id))
        assertThat(countDeadLetters()).isEqualTo(3)
    }

    private fun sendViaRabbitMq(objectLocation: ObjectLocation) {
        val proto = ObjectLocationProto.ObjectLocations.newBuilder().addObjectLocations(objectLocation.toProto()).build()
        sendViaRabbitMq(Message(proto.toByteArray()))
    }

    private fun sendViaRabbitMq(message: Message) {
        rabbitTemplate.send(RABBITMQ_EXCHANGE, OBJECT_LOCATION_SOURCE_ROUTING_KEY, message)
    }

    private fun countDeadLetters(): Int = countDeadLetters(rabbitTemplate)

    private fun countDeadLettersRetrying(): Int = countDeadLettersRetrying(rabbitTemplate)
}