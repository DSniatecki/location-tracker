package com.dsniatecki.locationtracker.archiver.objectlocation

import java.math.BigDecimal
import java.time.OffsetDateTime

data class ObjectLocation(
    val objectId: String,
    val receivedAt: OffsetDateTime,
    val latitude: BigDecimal,
    val longitude: BigDecimal
)