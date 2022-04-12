package com.dsniatecki.locationtracker.archiver

import java.util.UUID
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.test.context.DynamicPropertyRegistry
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.utility.DockerImageName
import org.testcontainers.utility.MountableFile

val invalidMsg = Message("INVALID_MSG".toByteArray())

fun generateId(): String = UUID.randomUUID().toString()

fun createDbTestContainer(): PostgreSQLContainer<Nothing> = PostgreSQLContainer<Nothing>(
    DockerImageName.parse(DB_CONTAINER)
        .asCompatibleSubstituteFor("postgres")
)

fun createRabbitMqTestContainer(): RabbitMQContainer = RabbitMQContainer(RABBITMQ_CONTAINER)
    .withEnv("RABBITMQ_SERVER_ADDITIONAL_ERL_ARGS", "-rabbitmq_management load_definitions \"/tmp/definitions.json\"")
    .withCopyFileToContainer(
        MountableFile.forClasspathResource(RABBITMQ_DEFINITIONS_PATH),
        "/tmp/definitions.json"
    )

fun registerDbProperties(dbContainer: PostgreSQLContainer<Nothing>, registry: DynamicPropertyRegistry) {
    val dbPort = dbContainer.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT)
    registry.add("spring.r2dbc.url") { "r2dbc:postgresql://${dbContainer.host}:$dbPort/${dbContainer.databaseName}" }
    registry.add("spring.r2dbc.username") { dbContainer.username }
    registry.add("spring.r2dbc.password") { dbContainer.password }
    registry.add("spring.flyway.url") { dbContainer.jdbcUrl }
    registry.add("spring.flyway.user") { dbContainer.username }
    registry.add("spring.flyway.password") { dbContainer.password }
}

fun registerRabbitMqProperties(rabbitMQContainer: RabbitMQContainer, registry: DynamicPropertyRegistry) {
    registry.add("spring.rabbitmq.host") { rabbitMQContainer.host }
    registry.add("spring.rabbitmq.port") { rabbitMQContainer.amqpPort }
}

fun cleanDb(databaseClient: DatabaseClient) {
    databaseClient.sql("DELETE FROM $OBJECT_LOCATION_TABLE WHERE true")
        .fetch()
        .one()
        .block()
}

fun waitUntilDbIsNotEmpty(databaseClient: DatabaseClient) {
    while (countDbElements(databaseClient) == 0) {
        Thread.sleep(10)
    }
}

fun countDbElements(databaseClient: DatabaseClient): Int =
    databaseClient.sql("SELECT count(*) FROM object_location")
        .map { row, _ -> row.get(0).toString().toInt() }
        .one()
        .block()!!

fun cleanQueues(
    rabbitTemplate: RabbitTemplate,
    queues: Iterable<String> = listOf(OBJECT_LOCATION_SOURCE_QUEUE, OBJECT_LOCATION_SOURCE_QUEUE, DEAD_LETTER_QUEUE)
) {
    queues.forEach { emptyQueue(it, rabbitTemplate) }
}

fun emptyQueue(queue: String, rabbitTemplate: AmqpTemplate) {
    while (rabbitTemplate.receive(queue) != null) {
        Unit
    }
}

fun countDeadLetters(rabbitTemplate: RabbitTemplate): Int {
    var numberOfMessages = 0;
    while (rabbitTemplate.receive(DEAD_LETTER_QUEUE) != null) {
        numberOfMessages++
    }
    return numberOfMessages
}

fun countDeadLettersRetrying(rabbitTemplate: RabbitTemplate): Int {
    var deadLetters: Int
    do {
        deadLetters = countDeadLetters(rabbitTemplate)
        if (deadLetters == 0)
            Thread.sleep(5)
    } while (deadLetters == 0)
    return deadLetters
}