package com.dsniatecki.locationtracker.archiver.objectlocation

import com.dsniatecki.locationtracker.archiver.message.ObjectLocationProto
import com.dsniatecki.locationtracker.archiver.message.utils.toBigDecimal
import com.dsniatecki.locationtracker.archiver.message.utils.toLocalDateTime
import com.dsniatecki.locationtracker.archiver.message.utils.toProtoDateTime
import com.dsniatecki.locationtracker.archiver.message.utils.toProtoDecimal

fun ObjectLocationProto.ObjectLocation.toObjectLocation(): ObjectLocation =
    ObjectLocation(
        objectId = this.objectId,
        receivedAt = this.receivedAt.toLocalDateTime(),
        latitude = this.latitude.toBigDecimal(),
        longitude = this.longitude.toBigDecimal()
    )

fun ObjectLocation.toProto(): ObjectLocationProto.ObjectLocation =
    ObjectLocationProto.ObjectLocation.newBuilder()
        .setObjectId(this.objectId)
        .setReceivedAt(this.receivedAt.toProtoDateTime())
        .setLatitude(this.latitude.toProtoDecimal())
        .setLongitude(this.longitude.toProtoDecimal())
        .build()
