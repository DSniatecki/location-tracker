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
import com.dsniatecki.locationtracker.archiver.cleanQueues
import com.dsniatecki.locationtracker.archiver.countDeadLettersRetrying
import com.dsniatecki.locationtracker.archiver.createDbTestContainer
import com.dsniatecki.locationtracker.archiver.createRabbitMqTestContainer
import com.dsniatecki.locationtracker.archiver.createTestObjectLocation
import com.dsniatecki.locationtracker.archiver.message.ObjectLocationProto
import com.dsniatecki.locationtracker.archiver.registerDbProperties
import com.dsniatecki.locationtracker.archiver.registerRabbitMqProperties
import java.util.concurrent.TimeUnit
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
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
internal class ObjectLocationListenersNoDbConnectionTest(
    @Autowired private val rabbitTemplate: RabbitTemplate
) {

    companion object {

        @Container
        private val dbContainer = createDbTestContainer()

        @Container
        private val rabbitMqContainer = createRabbitMqTestContainer()

        @DynamicPropertySource
        @JvmStatic
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registerDbProperties(dbContainer, registry)
            registerRabbitMqProperties(rabbitMqContainer, registry)
            registry.add("spring.r2dbc.url") { "r2dbc:postgresql://wrong_host:666/WRONG_DB" }
        }
    }

    @AfterEach
    fun clean() {
        cleanQueues(rabbitTemplate)
    }

    @Test
    @Timeout(value = TIMEOUT_VALUE_MILLIS, unit = TimeUnit.MILLISECONDS)
    fun `Should send to dead letter queue while due to db connection error - ObjectLocationSourceListener`() {
        sendViaRabbitMq(createTestObjectLocation())
        assertThat(countDeadLettersRetrying()).isEqualTo(1)
    }

    private fun sendViaRabbitMq(objectLocation: ObjectLocation) {
        val proto = ObjectLocationProto.ObjectLocations.newBuilder().addObjectLocations(objectLocation.toProto()).build()
        sendViaRabbitMq(Message(proto.toByteArray()))
    }

    private fun sendViaRabbitMq(message: Message) {
        rabbitTemplate.send(RABBITMQ_EXCHANGE, OBJECT_LOCATION_SOURCE_ROUTING_KEY, message)
    }

    private fun countDeadLettersRetrying(): Int = countDeadLettersRetrying(rabbitTemplate)
}