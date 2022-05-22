package com.dsniatecki.locationtracker.performer.config

import com.dsniatecki.locationtracker.archiver.ApiClient as ArchiverApiClient
import com.dsniatecki.locationtracker.storage.ApiClient as StorageApiClient
import com.dsniatecki.locationtracker.archiver.api.internal.ObjectLocationControllerApi
import com.dsniatecki.locationtracker.commons.utils.TimeSupplier
import com.dsniatecki.locationtracker.commons.utils.createTimeRecorderMetric
import com.dsniatecki.locationtracker.commons.utils.withCounter
import com.dsniatecki.locationtracker.performer.config.props.JobsProps
import com.dsniatecki.locationtracker.performer.locationsnapshot.LocationSnapshotMailCreator
import com.dsniatecki.locationtracker.performer.locationsnapshot.LocationSnapshotMailSender
import com.dsniatecki.locationtracker.performer.locationsnapshot.LocationSnapshotScheduledService
import com.dsniatecki.locationtracker.performer.locationsnapshot.LocationSnapshotSender
import com.dsniatecki.locationtracker.performer.sftp.SftpSender
import com.dsniatecki.locationtracker.storage.api.internal.ObjectControllerApi
import com.fasterxml.jackson.databind.ObjectMapper
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.context.annotation.Bean
import org.springframework.mail.javamail.JavaMailSender
import org.thymeleaf.TemplateEngine

class LocationSnapshotConfig(
    private val jobsProps: JobsProps,
    private val timeSupplier: TimeSupplier
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
    fun locationSnapshotMailCreator(mailTemplateEngine: TemplateEngine): LocationSnapshotMailCreator =
        LocationSnapshotMailCreator(mailTemplateEngine, jobsProps.locationSnapshot.mail.template)

    @Bean
    fun locationSnapshotMailSender(javaMailSender: JavaMailSender): LocationSnapshotMailSender =
        LocationSnapshotMailSender(javaMailSender, timeSupplier, jobsProps.locationSnapshot.mail)

    @Bean
    fun objectLocationScheduledService(
        objectController: ObjectControllerApi,
        objectLocationController: ObjectLocationControllerApi,
        locationSnapshotMailCreator: LocationSnapshotMailCreator,
        locationSnapshotMailSender: LocationSnapshotMailSender,
        locationSnapshotSender: LocationSnapshotSender,
        meterRegistry: MeterRegistry
    ): LocationSnapshotScheduledService =
        LocationSnapshotScheduledService(
            objectController = objectController,
            objectLocationController = objectLocationController,
            locationSnapshotSender = locationSnapshotSender,
            locationSnapshotMailCreator = locationSnapshotMailCreator,
            locationSnapshotMailSender = locationSnapshotMailSender,
            timeSupplier = timeSupplier,
            timeRecorder = meterRegistry.createTimeRecorderMetric(
                "location_snapshot_job_time",
                "Time of location snapshot job"
            ).withCounter(
                "location_snapshot_job_count",
                "Number of executed location snapshot jobs"
            ),
            props = jobsProps.locationSnapshot
        )
}