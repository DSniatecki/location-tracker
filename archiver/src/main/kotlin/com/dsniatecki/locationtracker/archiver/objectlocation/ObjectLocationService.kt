package com.dsniatecki.locationtracker.archiver.objectlocation

import java.time.Duration
import java.time.LocalDateTime
import reactor.core.publisher.Mono

class ObjectLocationService(
    private val objectLocationRepository: ObjectLocationRepository,
    private val defaultTolerance: Duration
) {

    fun getEffectiveAt(objectId: String, effectiveAt: LocalDateTime, tolerance: Duration? = null): Mono<ObjectLocation> =
        objectLocationRepository.findEffectiveAt(
            objectId = objectId,
            effectiveAt = effectiveAt,
            tolerance = tolerance ?: defaultTolerance
        ).map { mapFromRow(it) }

    fun saveAll(objectLocations: Iterable<ObjectLocation>): Mono<Unit> =
        objectLocationRepository.saveAll(objectLocations.map { mapToRow(it) })

    private fun mapFromRow(row: ObjectLocationRow): ObjectLocation =
        ObjectLocation(
            objectId = row.objectId,
            receivedAt = row.receivedAt,
            latitude = row.latitude,
            longitude = row.longitude
        )

    private fun mapToRow(objectLocation: ObjectLocation): ObjectLocationRow =
        ObjectLocationRow(
            objectId = objectLocation.objectId,
            receivedAt = objectLocation.receivedAt,
            latitude = objectLocation.latitude,
            longitude = objectLocation.longitude
        )
}