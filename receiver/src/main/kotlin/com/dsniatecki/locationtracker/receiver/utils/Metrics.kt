package com.dsniatecki.locationtracker.receiver.utils

import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import java.util.concurrent.atomic.LongAdder

private class CounterMetric(private val counter: LongAdder = LongAdder()) : Counter {
    fun count(): Long = counter.sum()

    override fun add(number: Int) {
        counter.add(number.toLong())
    }
}

fun createCounterMetric(name: String, description: String, meterRegistry: MeterRegistry): Counter {
    val operationsCounter = CounterMetric()
    Gauge.builder(name) { operationsCounter.count() }.description(description).register(meterRegistry)
    return operationsCounter
}
