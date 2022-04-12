package com.dsniatecki.locationtracker.commons.utils

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

interface TimeSupplier {
    fun now(): LocalDateTime
}