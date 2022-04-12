package com.dsniatecki.locationtracker.archiver

import com.dsniatecki.locationtracker.archiver.objectlocation.ObjectLocation
import java.math.BigDecimal
import java.time.LocalDateTime

val testTime: LocalDateTime = LocalDateTime.parse("2021-01-02T14:00:00.833306")

fun createTestObjectLocation(
    objectId: String = generateId(),
    receivedAt: LocalDateTime = testTime,
    latitude: BigDecimal = BigDecimal("24.12124212"),
    longitude: BigDecimal = BigDecimal("64.42127643"),
): ObjectLocation = ObjectLocation(
    objectId = objectId,
    receivedAt = receivedAt,
    latitude = latitude,
    longitude = longitude
)
