package com.dsniatecki.locationtracker.storage.`object`

import java.util.UUID

const val idLength = 36

const val minNameLength = 1
const val maxNameLength = 128

const val minImageUrlLength = 4
const val maxImageUrlLength = 256

fun generateId(): String = UUID.randomUUID().toString()

fun NewObject.validate(): NewObject {
    this.id?.let { validateId(it) }
    validateName(this.name)
    this.imageUrl?.let { validateImageUrl(it) }
    return this
}

fun ObjectUpdate.validate(): ObjectUpdate {
    validateName(this.name)
    this.imageUrl?.let { validateImageUrl(it) }
    return this
}

private fun validateId(id: String) {
    if (id.length != idLength) {
        throw IllegalStateException("Object id is not valid.")
    }
}

private fun validateName(name: String) {
    if (name.length < minNameLength || name.length > maxNameLength) {
        throw IllegalStateException("Object name is not valid.")
    }
}

private fun validateImageUrl(imageUrl: String) {
    if (imageUrl.length < minImageUrlLength || imageUrl.length > maxImageUrlLength) {
        throw IllegalStateException("Object image url is not valid.")
    }
}