package com.dsniatecki.locationtracker.storage.`object`

import com.dsniatecki.locationtracker.commons.utils.Counter
import com.dsniatecki.locationtracker.commons.utils.TimeRecorder
import com.dsniatecki.locationtracker.commons.utils.TimeSupplier
import com.dsniatecki.locationtracker.commons.utils.atZone
import com.dsniatecki.locationtracker.commons.utils.recorded
import java.time.LocalDateTime
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
    private val saveCounter: Counter,
    private val deleteTimeRecorder: TimeRecorder,
    private val deleteCounter: Counter,
) {

    fun findById(objectId: String): Mono<ObjectInstance> =
        objectRowRepository.findById(objectId)
            .doOnEach { findCounter.increment() }
            .recorded(findTimeRecorder)
            .map { mapFromRow(it) }

    fun findAll(): Flux<ObjectInstance> =
        objectRowRepository
            .findAll()
            .doFirst { findAllCounter.increment() }
            .recorded(findAllTimeRecorder)
            .map { mapFromRow(it) }

    fun save(objectInstance: ObjectInstance): Mono<ObjectInstance> =
        objectRowRepository.save(mapToRow(objectInstance))
            .doOnEach { saveCounter.increment() }
            .recorded(saveTimeRecorder)
            .map { mapFromRow(it) }

    fun delete(objectId: String, deleted_at: LocalDateTime): Mono<Unit> =
        objectRowRepository.delete(objectId, deleted_at)
            .doOnEach { deleteCounter.increment() }
            .recorded(deleteTimeRecorder)

    private fun mapFromRow(row: ObjectRow): ObjectInstance =
        timeSupplier.zoneOffset().let {
            ObjectInstance(
                id = row.id,
                name = row.name,
                imageUrl = row.imageUrl,
                createdAt = row.createdAt.atOffset(it),
                updatedAt = row.updatedAt?.atOffset(it)
            )
        }

    private fun mapToRow(objectInstance: ObjectInstance): ObjectRow =
        timeSupplier.zoneId().let {
            ObjectRow(
                id = objectInstance.id,
                name = objectInstance.name,
                imageUrl = objectInstance.imageUrl,
                createdAt = objectInstance.createdAt.atZone(it),
                updatedAt = objectInstance.updatedAt?.atZone(it),
                isDeleted = false
            )
        }
}