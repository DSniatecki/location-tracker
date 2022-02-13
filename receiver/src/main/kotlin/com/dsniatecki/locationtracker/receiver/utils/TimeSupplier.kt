package com.dsniatecki.locationtracker.receiver.utils

import java.time.LocalDateTime

interface TimeSupplier {
    fun now(): LocalDateTime
}