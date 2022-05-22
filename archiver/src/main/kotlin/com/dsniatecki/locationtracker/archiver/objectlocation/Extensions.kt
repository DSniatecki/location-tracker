package com.dsniatecki.locationtracker.archiver.objectlocation

import java.math.BigDecimal
import java.math.RoundingMode

private const val OBJECT_ID_LENGTH = 36

private val minLatitude = BigDecimal(-90)
private val maxLatitude = BigDecimal(90)

private val minLongitude = BigDecimal(-180)
private val maxLongitude = BigDecimal(180)

fun ObjectLocation.validate(): ObjectLocation {
    if (this.objectId.length != OBJECT_ID_LENGTH) {
        throw IllegalStateException("Object location objectId is not valid UUID.")
    }
    if (this.latitude < minLatitude || this.latitude > maxLatitude) {
        throw IllegalStateException("Object location latitude is not valid.")
    }
    if (this.longitude < minLongitude || this.longitude > maxLongitude) {
        throw IllegalStateException("Object location longitude is not valid.")
    }
    return this.copy(
        latitude = this.latitude.setScale(8, RoundingMode.UP),
        longitude = this.longitude.setScale(8, RoundingMode.UP)
    )
}