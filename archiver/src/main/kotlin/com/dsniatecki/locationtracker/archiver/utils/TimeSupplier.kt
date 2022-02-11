package com.dsniatecki.locationtracker.archiver.utils

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

interface TimeSupplier {
    fun now(): LocalDateTime
    fun zoneId(): ZoneId
    fun zoneOffset(): ZoneOffset
}