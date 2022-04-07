package com.dsniatecki.locationtracker.performer.locationsnapshot

import com.dsniatecki.locationtracker.archiver.api.ObjectLocationControllerApi
import com.dsniatecki.locationtracker.archiver.model.ObjectLocation
import com.dsniatecki.locationtracker.commons.utils.TimeRecorder
import com.dsniatecki.locationtracker.commons.utils.TimeSupplier
import com.dsniatecki.locationtracker.commons.utils.recorded
import com.dsniatecki.locationtracker.performer.config.props.LocationSnapshotJobProps
import com.dsniatecki.locationtracker.storage.api.ObjectControllerApi
import com.dsniatecki.locationtracker.storage.model.ObjectInstance
import java.time.LocalDateTime
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import reactor.core.publisher.Mono

class LocationSnapshotScheduledService(
    private val objectController: ObjectControllerApi,
    private val objectLocationController: ObjectLocationControllerApi,
    private val locationSnapshotSender: LocationSnapshotSender,
    private val timeSupplier: TimeSupplier,
    private val timeRecorder: TimeRecorder,
    private val props: LocationSnapshotJobProps,
) {

    companion object {
        private val logger = KotlinLogging.logger { }
    }

    @Scheduled(cron = "\${performer.jobs.location-snapshot.scheduler-cron}")
    fun execute() {
        val effectiveAt = timeSupplier.now()
        logger.info { "Starting location snapshot job ..." }
        logger.debug { "Calling Storage for objects with ids: ${props.objectIds} ..." }
        objectController.getObjects(props.objectIds)
            .collectList()
            .doOnSuccess { logger.debug { "Calling Archiver for object locations with ids: ${props.objectIds} ..." } }
            .flatMap { objects ->
                objectLocationController.getEffectiveObjectLocations(props.objectIds, effectiveAt, props.tolerance.toSeconds())
                    .collectList()
                    .map { mapToObjectLocationSnapshots(objects, it, effectiveAt) }
            }
            .map { locationSnapshotSender.send(it, effectiveAt) }
            .doOnError { logger.error(it) { "There was an error while executing location snapshot job: " } }
            .doOnSuccess {
                logger.info { "Location snapshot job was was successfully executed." }
            }.onErrorResume { Mono.empty() }
            .recorded(timeRecorder)
            .subscribe()
    }

    private fun mapToObjectLocationSnapshots(
        objectInstances: Iterable<ObjectInstance>,
        objectLocations: Iterable<ObjectLocation>,
        effectiveAt: LocalDateTime
    ): Iterable<LocationSnapshot> {
        val objectsMap = objectInstances.associateBy { it.id }
        val objectLocationsMap = objectLocations.associateBy { it.objectId }
        return props.objectIds.mapNotNull { id ->
            objectsMap[id]?.let { objectInstance ->
                objectLocationsMap[id]?.let { mapToLocationSnapshot(objectInstance, it, effectiveAt) }
            }
        }
    }

    private fun mapToLocationSnapshot(
        objectInstance: ObjectInstance,
        objectLocation: ObjectLocation,
        effectiveAt: LocalDateTime
    ): LocationSnapshot =
        LocationSnapshot(
            objectId = objectInstance.id,
            objectName = objectInstance.name,
            latitude = objectLocation.latitude,
            longitude = objectLocation.longitude,
            receivedAt = objectLocation.receivedAt,
            effectiveAt = effectiveAt
        )
}