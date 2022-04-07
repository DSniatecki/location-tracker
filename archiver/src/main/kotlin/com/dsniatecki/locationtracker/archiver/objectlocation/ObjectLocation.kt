package com.dsniatecki.locationtracker.archiver.objectlocation

import java.math.BigDecimal
import java.time.LocalDateTime

data class ObjectLocation(
    val objectId: String,
    val receivedAt: LocalDateTime,
    val latitude: BigDecimal,
    val longitude: BigDecimal
)