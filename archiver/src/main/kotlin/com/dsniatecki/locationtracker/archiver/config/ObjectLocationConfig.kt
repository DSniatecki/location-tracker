package com.dsniatecki.locationtracker.archiver.config

import com.dsniatecki.locationtracker.archiver.objectlocation.ObjectLocationRepository
import com.dsniatecki.locationtracker.archiver.objectlocation.ObjectLocationService
import com.dsniatecki.locationtracker.archiver.utils.TimeSupplier
import com.dsniatecki.locationtracker.archiver.utils.createCounterMetric
import com.dsniatecki.locationtracker.archiver.utils.createTimeRecorderMetric
import io.micrometer.core.instrument.MeterRegistry
import java.time.Duration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter
import org.springframework.r2dbc.core.DatabaseClient

@Configuration
class ObjectLocationConfig {

    @Bean
    fun objectLocationRepository(
        databaseClient: DatabaseClient,
        converter: MappingR2dbcConverter,
        metricsRegistry: MeterRegistry
    ): ObjectLocationRepository =
        ObjectLocationRepository(
            databaseClient = databaseClient,
            converter = converter,
            findCounter = createCounterMetric(
                "object_location_query_find_time",
                "Time of query responsible for finding object location",
                metricsRegistry
            ),
            findTimeRecorder = createTimeRecorderMetric(
                "object_location_query_find_count",
                "Number of executed queries responsible for finding object location",
                metricsRegistry
            ),
            saveCounter = createCounterMetric(
                "object_location_query_save_time",
                "Time of query responsible for saving object locations",
                metricsRegistry
            ),
            saveTimeRecorder = createTimeRecorderMetric(
                "object_location_query_save_count",
                "Number of executed queries responsible for saving object locations",
                metricsRegistry
            )
        )

    @Bean
    fun objectLocationService(
        objectLocationRepository: ObjectLocationRepository,
        timeSupplier: TimeSupplier,
        defaultTolerance: Duration
    ): ObjectLocationService =
        ObjectLocationService(
            objectLocationRepository = objectLocationRepository,
            timeSupplier = timeSupplier,
            defaultTolerance = defaultTolerance
        )
}
