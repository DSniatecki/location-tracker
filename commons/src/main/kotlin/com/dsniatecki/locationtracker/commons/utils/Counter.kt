package com.dsniatecki.locationtracker.commons.utils

@FunctionalInterface
interface Counter {
    fun add(number: Int)
    fun increment() {
        add(1)
    }
}

