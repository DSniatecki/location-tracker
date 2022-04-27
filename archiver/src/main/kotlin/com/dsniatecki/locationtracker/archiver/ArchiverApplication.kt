package com.dsniatecki.locationtracker.archiver

import com.dsniatecki.locationtracker.archiver.config.AmqpConfig
import com.dsniatecki.locationtracker.archiver.config.FlywayConfig
import com.dsniatecki.locationtracker.archiver.config.ObjectLocationConfig
import com.dsniatecki.locationtracker.archiver.config.SecurityConfig
import com.dsniatecki.locationtracker.archiver.config.UtilsConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(UtilsConfig::class, SecurityConfig::class, FlywayConfig::class, AmqpConfig::class, ObjectLocationConfig::class)
class ArchiverApplication

fun main(args: Array<String>) {
    runApplication<ArchiverApplication>(*args)
}
