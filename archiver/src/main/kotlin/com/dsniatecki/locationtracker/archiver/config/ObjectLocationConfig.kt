package com.dsniatecki.locationtracker.archiver.config

import com.dsniatecki.locationtracker.archiver.objectlocation.ObjectLocationRepository
import com.dsniatecki.locationtracker.archiver.objectlocation.ObjectLocationService
import com.dsniatecki.locationtracker.commons.utils.createTimeRecorderMetric
import com.dsniatecki.locationtracker.commons.utils.withCounter
import io.micrometer.core.instrument.MeterRegistry
import java.time.Duration
import org.springframework.context.annotation.Bean
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter
import org.springframework.r2dbc.core.DatabaseClient

class ObjectLocationConfig {

    @Bean
    fun objectLocationService(
        objectLocationRepository: ObjectLocationRepository,
        defaultTolerance: Duration
    ): ObjectLocationService =
        ObjectLocationService(objectLocationRepository, defaultTolerance)

    @Bean
    fun objectLocationRepository(
        databaseClient: DatabaseClient,
        converter: MappingR2dbcConverter,
        meterRegistry: MeterRegistry
    ): ObjectLocationRepository =
        ObjectLocationRepository(
            databaseClient = databaseClient,
            converter = converter,
            findTimeRecorder = meterRegistry.createTimeRecorderMetric(
                "object_location_query_find_time",
                "Time of query responsible for finding object location",
            ).withCounter(
                "object_location_query_find_count",
                "Number of executed queries responsible for finding object location"
            ),
            saveTimeRecorder = meterRegistry.createTimeRecorderMetric(
                "object_location_query_save_time",
                "Time of query responsible for saving object locations",
            ).withCounter(
                "object_location_query_save_count",
                "Number of executed queries responsible for saving object locations",
            )
        )
}
