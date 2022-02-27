package com.dsniatecki.locationtracker.performer.locationsnapshot

import java.math.BigDecimal
import java.time.LocalDateTime

data class LocationSnapshot(
    val objectId: String,
    val objectName: String,
    val latitude: BigDecimal,
    val longitude: BigDecimal,
    val effectiveAt: LocalDateTime,
    val receivedAt: LocalDateTime
)