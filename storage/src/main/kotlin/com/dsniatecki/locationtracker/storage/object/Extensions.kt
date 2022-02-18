package com.dsniatecki.locationtracker.storage.`object`

import java.util.UUID

const val minNameLength = 1
const val maxNameLength = 128

const val minImageUrlLength = 4
const val maxImageUrlLength = 256

fun generateId(): String = UUID.randomUUID().toString()

fun NewObject.validate(): NewObject {
    if (this.name.length < minNameLength || this.name.length > maxNameLength) {
        throw IllegalStateException("Object name is not valid.")
    }
    if (this.imageUrl != null && (this.imageUrl.length < minImageUrlLength || this.imageUrl.length > maxImageUrlLength)) {
        throw IllegalStateException("Object image url is not valid.")
    }
    return this
}