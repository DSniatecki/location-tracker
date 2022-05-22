package com.dsniatecki.locationtracker.storage.utils

import java.util.UUID

fun generateId(): String = UUID.randomUUID().toString()
