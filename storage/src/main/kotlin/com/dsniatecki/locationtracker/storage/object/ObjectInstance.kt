package com.dsniatecki.locationtracker.storage.`object`

import java.time.LocalDateTime

data class ObjectInstance(
    val id: String,
    val name: String,
    val imageUrl: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)