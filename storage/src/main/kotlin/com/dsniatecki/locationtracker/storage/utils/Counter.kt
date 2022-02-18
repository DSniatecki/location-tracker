package com.dsniatecki.locationtracker.storage.utils

@FunctionalInterface
interface Counter {
    fun add(number: Int)
    fun increment() {
        add(1)
    }
}

