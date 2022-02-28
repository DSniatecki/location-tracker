package com.dsniatecki.locationtracker.receiver

import java.util.UUID
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.utility.MountableFile

fun generateId(): String = UUID.randomUUID().toString()

fun createRabbitMqTestContainer(): RabbitMQContainer = RabbitMQContainer(RABBITMQ_CONTAINER)
    .withEnv("RABBITMQ_SERVER_ADDITIONAL_ERL_ARGS", "-rabbitmq_management load_definitions \"/tmp/definitions.json\"")
    .withCopyFileToContainer(
        MountableFile.forClasspathResource(RABBITMQ_DEFINITIONS_PATH),
        "/tmp/definitions.json"
    )

fun registerRabbitMqProperties(rabbitMQContainer: RabbitMQContainer, registry: DynamicPropertyRegistry) {
    registry.add("spring.rabbitmq.host") { rabbitMQContainer.host }
    registry.add("spring.rabbitmq.port") { rabbitMQContainer.amqpPort }
}

fun cleanQueues(
    rabbitTemplate: RabbitTemplate,
    queues: Iterable<String> = listOf(OBJECT_LOCATION_SOURCE_QUEUE)
) {
    queues.forEach { emptyQueue(it, rabbitTemplate) }
}

fun emptyQueue(queue: String, rabbitTemplate: AmqpTemplate) {
    while (rabbitTemplate.receive(queue) != null) { Unit }
}

fun countQueueRetrying(queue: String, rabbitTemplate: RabbitTemplate): Int {
    var deadLetters: Int
    do {
        deadLetters = countQueue(queue, rabbitTemplate)
        if (deadLetters == 0)
            Thread.sleep(5)
    } while (deadLetters == 0)
    return deadLetters
}

fun countQueue(queue: String, rabbitTemplate: RabbitTemplate): Int {
    var numberOfMessages = 0;
    while (rabbitTemplate.receive(queue) != null) { numberOfMessages++ }
    return numberOfMessages
}
