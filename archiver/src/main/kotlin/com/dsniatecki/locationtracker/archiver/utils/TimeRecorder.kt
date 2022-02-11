package com.dsniatecki.locationtracker.archiver.utils

import java.util.concurrent.TimeUnit

@FunctionalInterface
interface TimeRecorder {
    fun record(amount: Long, timeUnit: TimeUnit)
}