package com.dsniatecki.locationtracker.archiver.objectlocation

import com.dsniatecki.locationtracker.archiver.message.ObjectLocationProto
import com.dsniatecki.locationtracker.archiver.message.utils.toBigDecimal
import com.dsniatecki.locationtracker.archiver.message.utils.toProtoDecimal
import com.dsniatecki.locationtracker.archiver.message.utils.toUtcOffsetDateTime
import com.dsniatecki.locationtracker.archiver.message.utils.toUtcProtoDateTime
import com.dsniatecki.locationtracker.archiver.objectlocation.ObjectLocation

fun ObjectLocationProto.ObjectLocation.toObjectLocation(): ObjectLocation =
    ObjectLocation(
        objectId = this.objectId,
        receivedAt = this.receivedAt.toUtcOffsetDateTime(),
        latitude = this.latitude.toBigDecimal(),
        longitude = this.longitude.toBigDecimal()
    )

fun ObjectLocation.toProto(): ObjectLocationProto.ObjectLocation =
    ObjectLocationProto.ObjectLocation.newBuilder()
        .setObjectId(this.objectId)
        .setReceivedAt(this.receivedAt.toUtcProtoDateTime())
        .setLatitude(this.latitude.toProtoDecimal())
        .setLongitude(this.longitude.toProtoDecimal())
        .build()
