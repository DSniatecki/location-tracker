package com.dsniatecki.locationtracker.receiver

import com.dsniatecki.locationtracker.receiver.config.AmqpConfig
import com.dsniatecki.locationtracker.receiver.config.ObjectLocationConfig
import com.dsniatecki.locationtracker.receiver.config.SecurityConfig
import com.dsniatecki.locationtracker.receiver.config.UtilsConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(UtilsConfig::class, SecurityConfig::class, AmqpConfig::class, ObjectLocationConfig::class)
class ReceiverApplication

fun main(args: Array<String>) {
    runApplication<ReceiverApplication>(*args)
}
