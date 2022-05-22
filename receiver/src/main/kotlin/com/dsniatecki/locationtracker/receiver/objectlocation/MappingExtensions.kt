package com.dsniatecki.locationtracker.receiver.objectlocation

import com.dsniatecki.locationtracker.receiver.model.internal.ObjectLocationDto as ObjectLocationInternalDto
import com.dsniatecki.locationtracker.receiver.model.pub.ObjectLocationDto as ObjectLocationPublicDto
import java.math.BigDecimal
import java.math.RoundingMode

fun ObjectLocationInternalDto.toObjectLocation(): ObjectLocation =
    ObjectLocation(
        objectId = this.objectId,
        latitude = this.latitude.roundToCoordinate(),
        longitude = this.longitude.roundToCoordinate()
    )

fun ObjectLocationPublicDto.toObjectLocation(): ObjectLocation =
    ObjectLocation(
        objectId = this.objectId,
        latitude = this.latitude.roundToCoordinate(),
        longitude = this.longitude.roundToCoordinate()
    )

private fun BigDecimal.roundToCoordinate(): BigDecimal = this.setScale(8, RoundingMode.UP)
