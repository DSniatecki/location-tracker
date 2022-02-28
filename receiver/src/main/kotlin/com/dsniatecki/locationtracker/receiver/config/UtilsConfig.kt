package com.dsniatecki.locationtracker.receiver.config

import com.dsniatecki.locationtracker.commons.utils.TimeSupplier
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean

class UtilsConfig(
    @Value("\${receiver.time-zone.id}") private val timeZoneId: String,
    @Value("\${receiver.time-zone.offset}") private val timeZoneOffset: Int
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
