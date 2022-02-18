package com.dsniatecki.locationtracker.storage.config

import com.dsniatecki.locationtracker.storage.utils.TimeSupplier
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean

class UtilsConfig(
    @Value("\${storage.time-zone.id}") private val timeZoneId: String,
    @Value("\${storage.time-zone.offset}") private val timeZoneOffset: Int
) {

    private val zoneId = ZoneId.of(timeZoneId)
    private val zoneOffset = ZoneOffset.ofHours(timeZoneOffset)

    @Bean
    fun timeSupplier(): TimeSupplier = object : TimeSupplier {
        override fun now(): LocalDateTime = LocalDateTime.now(zoneId)
        override fun zoneId(): ZoneId = zoneId
        override fun zoneOffset(): ZoneOffset = zoneOffset
    }
}
