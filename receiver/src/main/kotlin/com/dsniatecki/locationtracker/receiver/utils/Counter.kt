package com.dsniatecki.locationtracker.receiver.utils

@FunctionalInterface
interface Counter {
    fun add(number: Int)
    fun increment() {
        add(1)
    }
}

