package com.dsniatecki.locationtracker.storage.config

import com.dsniatecki.locationtracker.commons.utils.TimeSupplier
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean

class UtilsConfig(@Value("\${storage.time-zone-id}") private val timeZoneId: String) {

    private val zoneId = ZoneId.of(timeZoneId)

    @Bean
    fun timeSupplier(): TimeSupplier = object : TimeSupplier {
        override fun now(): LocalDateTime = LocalDateTime.now(zoneId)
    }
}
