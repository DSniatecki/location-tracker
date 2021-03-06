package com.dsniatecki.locationtracker.performer

import com.dsniatecki.locationtracker.performer.config.LocationSnapshotConfig
import com.dsniatecki.locationtracker.performer.config.UtilsConfig
import com.dsniatecki.locationtracker.performer.config.props.JobsProps
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@Import(UtilsConfig::class, LocationSnapshotConfig::class)
@EnableScheduling
@EnableConfigurationProperties(JobsProps::class)
class PerformerApplication

fun main(args: Array<String>) {
    runApplication<PerformerApplication>(*args)
}
