package com.dsniatecki.locationtracker.receiver.config

import com.dsniatecki.locationtracker.commons.utils.TimeSupplier
import java.time.LocalDateTime
import java.time.ZoneId
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean

class UtilsConfig(
    @Value("\${receiver.time-zone-id}") private val timeZoneId: String,
) {

    private val zoneId = ZoneId.of(timeZoneId)

    @Bean
    fun timeSupplier(): TimeSupplier = object : TimeSupplier {
        override fun now(): LocalDateTime = LocalDateTime.now(zoneId)
    }
}
