package com.dsniatecki.locationtracker.archiver.config

import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FlywayConfig(
    @Value("\${spring.flyway.url}") private val url: String,
    @Value("\${spring.flyway.user}") private val user: String,
    @Value("\${spring.flyway.password}") private val password: String,
    @Value("\${spring.flyway.locations}") private val locations: String
) {

    @Bean(initMethod = "migrate")
    fun flyway(): Flyway =
        Flyway(
            Flyway.configure()
                .baselineOnMigrate(true)
                .locations(locations)
                .dataSource(url, user, password)
        )
}
