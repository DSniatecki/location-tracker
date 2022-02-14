package com.dsniatecki.locationtracker.receiver.objectlocation

import java.math.BigDecimal

data class ObjectLocation(
    val objectId: String,
    val latitude: BigDecimal,
    val longitude: BigDecimal
)
