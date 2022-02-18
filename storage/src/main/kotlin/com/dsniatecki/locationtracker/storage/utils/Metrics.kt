package com.dsniatecki.locationtracker.storage.utils

import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.LongAdder
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

private class TimeRecorderMetric(private val timer: Timer) : TimeRecorder {
    override fun record(amount: Long, timeUnit: TimeUnit) {
        timer.record(amount, timeUnit)
    }
}

private class CounterMetric(private val counter: LongAdder = LongAdder()) : Counter {
    fun count(): Long = counter.sum()

    override fun add(number: Int) {
        counter.add(number.toLong())
    }
}

fun createTimeRecorderMetric(name: String, description: String, meterRegistry: MeterRegistry): TimeRecorder =
    TimeRecorderMetric(
        Timer.builder(name)
            .description(description)
            .publishPercentileHistogram()
            .register(meterRegistry)
    )

fun createCounterMetric(name: String, description: String, meterRegistry: MeterRegistry): Counter {
    val operationsCounter = CounterMetric()
    Gauge.builder(name) { operationsCounter.count() }.description(description).register(meterRegistry)
    return operationsCounter
}

fun <T> Mono<T>.recorded(timeRecorder: TimeRecorder): Mono<T> {
    val start = System.currentTimeMillis()
    return this.doOnSuccess { timeRecorder.record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS) }
}

fun <T> Flux<T>.recorded(timeRecorder: TimeRecorder): Flux<T> {
    val start = System.currentTimeMillis()
    return this.doFinally { timeRecorder.record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS) }
}
