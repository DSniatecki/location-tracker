package com.dsniatecki.locationtracker.archiver.objectlocation

import com.dsniatecki.locationtracker.archiver.model.internal.ObjectLocationDto as ObjectLocationInternalDto
import com.dsniatecki.locationtracker.archiver.model.pub.ObjectLocationDto as ObjectLocationPublicDto
import java.math.BigDecimal
import java.math.RoundingMode

fun ObjectLocation.toInternalDto(): ObjectLocationInternalDto =
    ObjectLocationInternalDto()
        .objectId(this.objectId)
        .receivedAt(this.receivedAt)
        .latitude(this.latitude)
        .longitude(this.longitude)

fun ObjectLocation.toPublicDto(): ObjectLocationPublicDto =
    ObjectLocationPublicDto()
        .objectId(this.objectId)
        .receivedAt(this.receivedAt)
        .latitude(this.latitude)
        .longitude(this.longitude)

fun ObjectLocationInternalDto.toObjectLocation(): ObjectLocation =
    ObjectLocation(
        objectId = this.objectId,
        receivedAt = this.receivedAt,
        latitude = this.latitude.roundToCoordinate(),
        longitude = this.longitude.roundToCoordinate()
    )

fun ObjectLocationPublicDto.toObjectLocation(): ObjectLocation =
    ObjectLocation(
        objectId = this.objectId,
        receivedAt = this.receivedAt,
        latitude = this.latitude.roundToCoordinate(),
        longitude = this.longitude.roundToCoordinate()
    )

private fun BigDecimal.roundToCoordinate(): BigDecimal = this.setScale(8, RoundingMode.UP)
