package com.dsniatecki.locationtracker.storage.`object`

import java.time.OffsetDateTime

data class ExistingObject(
    val id: String,
    val name: String,
    val imageUrl: String? = null,
    val createdAt: OffsetDateTime
)