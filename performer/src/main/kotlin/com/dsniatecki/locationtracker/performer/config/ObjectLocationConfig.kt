package com.dsniatecki.locationtracker.performer.config

import com.dsniatecki.locationtracker.archiver.ApiClient as ArchiverApiClient
import com.dsniatecki.locationtracker.storage.ApiClient as StorageApiClient
import com.dsniatecki.locationtracker.archiver.api.ObjectLocationControllerApi
import com.dsniatecki.locationtracker.commons.utils.TimeSupplier
import com.dsniatecki.locationtracker.performer.config.props.JobsProps
import com.dsniatecki.locationtracker.performer.locationsnapshot.LocationSnapshotScheduledService
import com.dsniatecki.locationtracker.performer.locationsnapshot.LocationSnapshotSender
import com.dsniatecki.locationtracker.performer.sftp.SftpSender
import com.dsniatecki.locationtracker.storage.api.ObjectControllerApi
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean

class ObjectLocationConfig(
    private val jobsProps: JobsProps
) {

    @Bean
    fun objectControllerApi(storageApiClient: StorageApiClient): ObjectControllerApi =
        ObjectControllerApi(storageApiClient)

    @Bean
    fun objectLocationControllerApi(archiverApiClient: ArchiverApiClient): ObjectLocationControllerApi =
        ObjectLocationControllerApi(archiverApiClient)

    @Bean
    fun locationSnapshotSender(
        sftpSender: SftpSender,
        objectMapper: ObjectMapper
    ): LocationSnapshotSender =
        LocationSnapshotSender(
            sftpSender = sftpSender,
            sftpDestination = jobsProps.locationSnapshot.sftp,
            objectMapper = objectMapper
        )

    @Bean
    fun objectLocationScheduledService(
        objectController: ObjectControllerApi,
        objectLocationController: ObjectLocationControllerApi,
        timeSupplier: TimeSupplier,
        locationSnapshotSender: LocationSnapshotSender
    ): LocationSnapshotScheduledService =
        LocationSnapshotScheduledService(
            objectController = objectController,
            objectLocationController = objectLocationController,
            locationSnapshotSender = locationSnapshotSender,
            timeSupplier = timeSupplier,
            props = jobsProps.locationSnapshot
        )
}