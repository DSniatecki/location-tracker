package com.dsniatecki.locationtracker.archiver.message.utils;

import com.dsniatecki.locationtracker.archiver.message.Common
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset

private val utcZoneId = ZoneId.of("UTC")

fun Common.DateTime.toLocalDateTime(): LocalDateTime =
    LocalDateTime.of(
        LocalDate.of(this.date.year, this.date.month, this.date.day),
        LocalTime.of(this.time.hour, this.time.minute, this.time.second, this.time.nano)
    )

fun Common.DateTime.toUtcOffsetDateTime(): OffsetDateTime =
    OffsetDateTime.ofInstant(this.toLocalDateTime().toInstant(ZoneOffset.UTC), utcZoneId)

fun LocalDateTime.toProtoDateTime(): Common.DateTime =
    Common.DateTime.newBuilder()
        .setDate(
            Common.Date.newBuilder()
                .setYear(this.year)
                .setMonth(this.monthValue)
                .setDay(this.dayOfMonth)
                .build()
        ).setTime(
            Common.Time.newBuilder()
                .setHour(this.hour)
                .setMinute(this.minute)
                .setSecond(this.second)
                .setNano(this.nano)
                .build()
        ).build()

fun OffsetDateTime.toUtcProtoDateTime(): Common.DateTime =
    this.atZoneSameInstant(utcZoneId).toLocalDateTime().toProtoDateTime()

fun Common.Decimal.toBigDecimal(): BigDecimal = BigDecimal(this.unscaled.toBigInteger(), this.scale)

fun BigDecimal.toProtoDecimal(): Common.Decimal =
    Common.Decimal.newBuilder()
        .setUnscaled(this.unscaledValue().longValueExact())
        .setScale(this.scale())
        .build()
