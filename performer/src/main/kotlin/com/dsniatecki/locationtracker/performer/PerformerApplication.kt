package com.dsniatecki.locationtracker.performer

import com.dsniatecki.locationtracker.performer.config.ObjectLocationConfig
import com.dsniatecki.locationtracker.performer.config.UtilsConfig
import com.dsniatecki.locationtracker.performer.config.props.JobsProps
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@Import(UtilsConfig::class, ObjectLocationConfig::class)
@EnableScheduling
@EnableConfigurationProperties(JobsProps::class)
class ReceiverApplication

fun main(args: Array<String>) {
    runApplication<ReceiverApplication>(*args)
}
