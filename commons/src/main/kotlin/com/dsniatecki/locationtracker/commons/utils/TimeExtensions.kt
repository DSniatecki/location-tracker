package com.dsniatecki.locationtracker.commons.utils

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId

fun OffsetDateTime.atZone(zoneId: ZoneId): LocalDateTime = this.atZoneSameInstant(zoneId).toLocalDateTime()
