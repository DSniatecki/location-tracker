package com.dsniatecki.locationtracker.receiver.objectlocation

import com.dsniatecki.locationtracker.receiver.OBJECT_LOCATION_EXCHANGE
import com.dsniatecki.locationtracker.receiver.OBJECT_LOCATION_SOURCE_QUEUE
import com.dsniatecki.locationtracker.receiver.OBJECT_LOCATION_SOURCE_ROUTING_KEY
import com.dsniatecki.locationtracker.receiver.RABBITMQ_PASSWORD
import com.dsniatecki.locationtracker.receiver.RABBITMQ_USERNAME
import com.dsniatecki.locationtracker.receiver.RABBITMQ_VIRTUAL_HOST
import com.dsniatecki.locationtracker.receiver.ReceiverApplication
import com.dsniatecki.locationtracker.receiver.cleanQueues
import com.dsniatecki.locationtracker.receiver.countQueueRetrying
import com.dsniatecki.locationtracker.receiver.createRabbitMqTestContainer
import com.dsniatecki.locationtracker.receiver.generateId
import com.dsniatecki.locationtracker.receiver.registerRabbitMqProperties
import java.math.BigDecimal
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
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
        "spring.rabbitmq.object-location.exchange=$OBJECT_LOCATION_EXCHANGE",
        "spring.rabbitmq.object-location.store.routing-key=$OBJECT_LOCATION_SOURCE_ROUTING_KEY",
        "spring.rabbitmq.ssl.enabled=false"
    ],
    classes = [ReceiverApplication::class])
@Testcontainers
internal class ObjectLocationServiceTest(
    @Autowired private val objectLocationService: ObjectLocationService,
    @Autowired private val rabbitTemplate: RabbitTemplate
) {
    companion object {
        @Container
        private val rabbitMqContainer = createRabbitMqTestContainer()

        @DynamicPropertySource
        @JvmStatic
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registerRabbitMqProperties(rabbitMqContainer, registry)
        }
    }

    @AfterEach
    fun clean() {
        cleanQueues(rabbitTemplate)
    }

    @Test
    fun `Should send object location to source queue in single batch`() {
        objectLocationService.save(createTestObjectLocation()).block()
        assertThat(countQueueRetrying(OBJECT_LOCATION_SOURCE_QUEUE, rabbitTemplate)).isEqualTo(1)
    }

    @Test
    fun `Should send 5 object locations to source queue in single batch`() {
        repeat(5) { objectLocationService.save(createTestObjectLocation()).block() }
        assertThat(countQueueRetrying(OBJECT_LOCATION_SOURCE_QUEUE, rabbitTemplate)).isEqualTo(1)
    }

    fun createTestObjectLocation(
        objectId: String = generateId(),
        latitude: BigDecimal = BigDecimal("24.12124212"),
        longitude: BigDecimal = BigDecimal("64.42127643"),
    ): ObjectLocation = ObjectLocation(
        objectId = objectId,
        latitude = latitude,
        longitude = longitude
    )
}