package com.dsniatecki.locationtracker.storage.`object`

import com.dsniatecki.locationtracker.storage.utils.Counter
import com.dsniatecki.locationtracker.storage.utils.TimeRecorder
import com.dsniatecki.locationtracker.storage.utils.TimeSupplier
import com.dsniatecki.locationtracker.storage.utils.atZone
import com.dsniatecki.locationtracker.storage.utils.recorded
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class ObjectRepository(
    private val objectRowRepository: ObjectRowRepository,
    private val timeSupplier: TimeSupplier,
    private val findTimeRecorder: TimeRecorder,
    private val findCounter: Counter,
    private val findAllTimeRecorder: TimeRecorder,
    private val findAllCounter: Counter,
    private val saveTimeRecorder: TimeRecorder,
    private val saveCounter: Counter
) {

    fun findById(objectId: String): Mono<ExistingObject> =
        objectRowRepository.findById(objectId)
            .doOnEach { findCounter.increment() }
            .recorded(findTimeRecorder)
            .map { mapFromRow(it) }

    fun findAll(): Flux<ExistingObject> =
        objectRowRepository
            .findAll()
            .doFirst { findAllCounter.increment() }
            .recorded(findAllTimeRecorder)
            .map { mapFromRow(it) }

    fun save(existingObject: ExistingObject): Mono<ExistingObject> =
        objectRowRepository.save(mapToRow(existingObject))
            .doOnEach { saveCounter.increment() }
            .recorded(saveTimeRecorder)
            .map { mapFromRow(it) }

    private fun mapFromRow(row: ObjectRow): ExistingObject =
        ExistingObject(
            id = row.id,
            name = row.name,
            imageUrl = row.imageUrl,
            createdAt = row.createdAt.atOffset(timeSupplier.zoneOffset())
        )

    private fun mapToRow(existingObject: ExistingObject): ObjectRow =
        ObjectRow(
            id = existingObject.id,
            name = existingObject.name,
            imageUrl = existingObject.imageUrl,
            createdAt = existingObject.createdAt.atZone(timeSupplier.zoneId())
        )
}