package com.dsniatecki.locationtracker.storage.`object`

import java.time.OffsetDateTime

data class ObjectInstance(
    val id: String,
    val name: String,
    val imageUrl: String?,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime?
)