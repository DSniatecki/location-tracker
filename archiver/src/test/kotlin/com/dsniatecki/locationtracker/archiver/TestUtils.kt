package com.dsniatecki.locationtracker.archiver

import java.util.UUID
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.test.context.DynamicPropertyRegistry
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

fun generateId(): String = UUID.randomUUID().toString()

fun cleanDb(databaseClient: DatabaseClient) {
    databaseClient.sql("DELETE FROM $OBJECT_LOCATION_TABLE WHERE true")
        .fetch()
        .one()
        .block()
}

fun createDbTestContainer(): PostgreSQLContainer<Nothing> = PostgreSQLContainer<Nothing>(
    DockerImageName.parse(DB_CONTAINER)
        .asCompatibleSubstituteFor("postgres")
)

fun registerDbProperties(registry: DynamicPropertyRegistry, dbContainer: PostgreSQLContainer<Nothing>) {
    val dbPort = dbContainer.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT)
    registry.add("spring.r2dbc.url") { "r2dbc:postgresql://${dbContainer.host}:$dbPort/${dbContainer.databaseName}" }
    registry.add("spring.r2dbc.username") { dbContainer.username }
    registry.add("spring.r2dbc.password") { dbContainer.password }
    registry.add("spring.flyway.url") { dbContainer.jdbcUrl }
    registry.add("spring.flyway.user") { dbContainer.username }
    registry.add("spring.flyway.password") { dbContainer.password }
}
