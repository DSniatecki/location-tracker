package com.dsniatecki.locationtracker.storage.`object`

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
    private val findMultipleTimeRecorder: TimeRecorder,
    private val saveTimeRecorder: TimeRecorder,
    private val deleteTimeRecorder: TimeRecorder,
) {

    fun findById(objectId: String): Mono<ObjectInstance> =
        objectRowRepository.findById(objectId)
            .recorded(findTimeRecorder)
            .map { mapFromRow(it) }

    fun findByIds(objectIds: Set<String>): Flux<ObjectInstance> =
        objectRowRepository
            .findByIds(objectIds)
            .recorded(findMultipleTimeRecorder)
            .map { mapFromRow(it) }

    fun save(objectInstance: ObjectInstance): Mono<ObjectInstance> =
        objectRowRepository.save(mapToRow(objectInstance))
            .recorded(saveTimeRecorder)
            .map { mapFromRow(it) }

    fun delete(objectId: String, deleted_at: LocalDateTime): Mono<Unit> =
        objectRowRepository.delete(objectId, deleted_at)
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