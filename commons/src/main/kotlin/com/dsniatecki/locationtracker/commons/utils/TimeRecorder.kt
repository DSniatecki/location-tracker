package com.dsniatecki.locationtracker.commons.utils

import java.util.concurrent.TimeUnit

@FunctionalInterface
interface TimeRecorder {
    fun record(amount: Long, timeUnit: TimeUnit)
}