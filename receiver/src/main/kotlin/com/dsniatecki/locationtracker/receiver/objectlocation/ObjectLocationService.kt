package com.dsniatecki.locationtracker.receiver.objectlocation

import com.dsniatecki.locationtracker.commons.utils.Counter
import com.dsniatecki.locationtracker.commons.utils.TimeSupplier
import reactor.core.publisher.Mono

class ObjectLocationService(
    private val objectLocationSender: ObjectLocationSender,
    private val timeSupplier: TimeSupplier,
    private val sentCounter: Counter
) {

    fun save(objectLocation: ObjectLocation): Mono<Unit> =
        Mono.just(timeSupplier.now())
            .map { objectLocationSender.send(objectLocation, it) }
            .doOnSuccess { sentCounter.increment() }
            .flatMap { Mono.empty() }
}