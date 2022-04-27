package com.dsniatecki.locationtracker.storage

import com.dsniatecki.locationtracker.storage.config.FlywayConfig
import com.dsniatecki.locationtracker.storage.config.ObjectConfig
import com.dsniatecki.locationtracker.storage.config.SecurityConfig
import com.dsniatecki.locationtracker.storage.config.UtilsConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(UtilsConfig::class, SecurityConfig::class, FlywayConfig::class, ObjectConfig::class)
class StorageApplication

fun main(args: Array<String>) {
    runApplication<StorageApplication>(*args)
}
