package com.dsniatecki.locationtracker.archiver.objectlocation

import com.dsniatecki.locationtracker.archiver.utils.TimeSupplier
import com.dsniatecki.locationtracker.archiver.utils.atZone
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime
import reactor.core.publisher.Mono

class ObjectLocationService(
    private val objectLocationRepository: ObjectLocationRepository,
    private val timeSupplier: TimeSupplier,
    private val defaultTolerance: Duration
) {

    fun getEffectiveAt(objectId: String, effectiveAt: LocalDateTime, tolerance: Duration? = null): Mono<ObjectLocation> =
        objectLocationRepository.findEffectiveAt(
            objectId = objectId,
            effectiveAt = effectiveAt,
            tolerance = tolerance ?: defaultTolerance
        ).map { mapFromRow(it) }

    fun getEffectiveAt(objectId: String, effectiveAt: OffsetDateTime, tolerance: Duration? = null): Mono<ObjectLocation> =
        objectLocationRepository.findEffectiveAt(
            objectId = objectId,
            effectiveAt = effectiveAt.atZone(timeSupplier.zoneId()),
            tolerance = tolerance ?: defaultTolerance
        ).map { mapFromRow(it) }

    fun saveAll(objectLocations: Iterable<ObjectLocation>): Mono<Unit> =
        objectLocationRepository.saveAll(objectLocations.map { mapToRow(it) })

    private fun mapFromRow(row: ObjectLocationRow): ObjectLocation =
        ObjectLocation(
            objectId = row.objectId,
            receivedAt = row.receivedAt.atOffset(timeSupplier.zoneOffset()),
            latitude = row.latitude,
            longitude = row.longitude
        )

    private fun mapToRow(objectLocation: ObjectLocation): ObjectLocationRow =
        ObjectLocationRow(
            objectId = objectLocation.objectId,
            receivedAt = objectLocation.receivedAt.atZone(timeSupplier.zoneOffset()),
            latitude = objectLocation.latitude,
            longitude = objectLocation.longitude
        )
}