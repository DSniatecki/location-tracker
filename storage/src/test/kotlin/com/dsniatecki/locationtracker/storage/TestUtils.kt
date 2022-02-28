package com.dsniatecki.locationtracker.storage

import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.test.context.DynamicPropertyRegistry
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import reactor.core.publisher.Flux

private const val DB_CONTAINER = "postgres:14-alpine"
private const val OBJECT_TABLE = "object"

fun createDbTestContainer(): PostgreSQLContainer<Nothing> =
    PostgreSQLContainer<Nothing>(DockerImageName.parse(DB_CONTAINER).asCompatibleSubstituteFor("postgres"))

fun registerDbProperties(dbContainer: PostgreSQLContainer<Nothing>, registry: DynamicPropertyRegistry) {
    val dbPort = dbContainer.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT)
    registry.add("spring.r2dbc.url") { "r2dbc:postgresql://${dbContainer.host}:$dbPort/${dbContainer.databaseName}" }
    registry.add("spring.r2dbc.username") { dbContainer.username }
    registry.add("spring.r2dbc.password") { dbContainer.password }
    registry.add("spring.flyway.url") { dbContainer.jdbcUrl }
    registry.add("spring.flyway.user") { dbContainer.username }
    registry.add("spring.flyway.password") { dbContainer.password }
}

fun cleanDb(databaseClient: DatabaseClient) {
    databaseClient.sql("DELETE FROM $OBJECT_TABLE WHERE true")
        .fetch()
        .one()
        .block()
}

fun <T> Flux<T>.toList(): List<T> = this.toIterable().toList()
