package com.dsniatecki.locationtracker.archiver.config

import com.dsniatecki.locationtracker.commons.utils.TimeSupplier
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean

class UtilsConfig(
    @Value("\${archiver.tolerance}") private val defaultTolerance: Duration,
    @Value("\${archiver.time-zone.id}") private val timeZoneId: String,
    @Value("\${archiver.time-zone.offset}") private val timeZoneOffset: Int
) {

    private val zoneId = ZoneId.of(timeZoneId)
    private val zoneOffset = ZoneOffset.ofHours(timeZoneOffset)

    @Bean
    fun defaultTolerance(): Duration = defaultTolerance

    @Bean
    fun timeSupplier(): TimeSupplier = object : TimeSupplier {
        override fun now(): LocalDateTime = LocalDateTime.now(zoneId)
        override fun zoneId(): ZoneId = zoneId
        override fun zoneOffset(): ZoneOffset = zoneOffset
    }
}
