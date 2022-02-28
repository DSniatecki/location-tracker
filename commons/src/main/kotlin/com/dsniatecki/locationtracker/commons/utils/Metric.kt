package com.dsniatecki.locationtracker.commons.utils

interface Measure{
    fun add(number: Int)
    fun increment() {
        add(1)
    }
}

