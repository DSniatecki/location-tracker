package com.dsniatecki.locationtracker.commons.utils

import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.LongAdder
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class TimeRecorderMetric(private val timer: Timer, val meterRegistry: MeterRegistry) : TimeRecorder {
    override fun record(amount: Long, timeUnit: TimeUnit) {
        timer.record(amount, timeUnit)
    }
}

private class TimeRecorderWithCounterMetric(
    private val timeRecorder: TimeRecorder,
    private val counter: Counter
) : TimeRecorder {
    override fun record(amount: Long, timeUnit: TimeUnit) {
        counter.increment()
        timeRecorder.record(amount, timeUnit)
    }
}

private class CounterMetric(private val counter: LongAdder = LongAdder()) : Counter {
    fun count(): Long = counter.sum()

    override fun add(number: Int) {
        counter.add(number.toLong())
    }
}

fun MeterRegistry.createTimeRecorderMetric(name: String, description: String): TimeRecorderMetric =
    TimeRecorderMetric(
        Timer.builder(name)
            .description(description)
            .publishPercentileHistogram()
            .register(this),
        this
    )

fun MeterRegistry.createCounterMetric(name: String, description: String): Counter {
    val operationsCounter = CounterMetric()
    Gauge.builder(name) { operationsCounter.count() }.description(description).register(this)
    return operationsCounter
}

fun TimeRecorderMetric.withCounter(name: String, description: String): TimeRecorder =
    TimeRecorderWithCounterMetric(this, this.meterRegistry.createCounterMetric(name, description))

fun <T> Mono<T>.recorded(timeRecorder: TimeRecorder): Mono<T> {
    val start = System.currentTimeMillis()
    return this.doOnSuccess { timeRecorder.record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS) }
}

fun <T> Flux<T>.recorded(timeRecorder: TimeRecorder): Flux<T> {
    val start = System.currentTimeMillis()
    return this.doFinally { timeRecorder.record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS) }
}
