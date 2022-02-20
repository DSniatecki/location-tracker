package com.dsniatecki.locationtracker.storage.config

import com.dsniatecki.locationtracker.commons.utils.TimeSupplier
import com.dsniatecki.locationtracker.commons.utils.createCounterMetric
import com.dsniatecki.locationtracker.commons.utils.createTimeRecorderMetric
import com.dsniatecki.locationtracker.storage.`object`.ObjectRepository
import com.dsniatecki.locationtracker.storage.`object`.ObjectRowRepository
import com.dsniatecki.locationtracker.storage.`object`.ObjectService
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.context.annotation.Bean

class ObjectConfig {

    @Bean
    fun objectService(objectRepository: ObjectRepository, timeSupplier: TimeSupplier): ObjectService =
        ObjectService(objectRepository, timeSupplier)

    @Bean
    fun objectRepository(
        objectRowRepository: ObjectRowRepository,
        timeSupplier: TimeSupplier, meterRegistry:
        MeterRegistry
    ): ObjectRepository =
        ObjectRepository(
            objectRowRepository = objectRowRepository,
            timeSupplier = timeSupplier,
            findTimeRecorder = createTimeRecorderMetric(
                "object_query_find_time",
                "Time of query responsible for finding object",
                meterRegistry
            ),
            findCounter = createCounterMetric(
                "object_query_find_count",
                "Number of executed queries responsible for finding object",
                meterRegistry
            ),
            findAllTimeRecorder = createTimeRecorderMetric(
                "object_query_find_all_time",
                "Time of query responsible for finding all objects",
                meterRegistry
            ),
            findAllCounter = createCounterMetric(
                "object_query_find_all_count",
                "Number of executed queries responsible for finding all objects",
                meterRegistry
            ),
            saveTimeRecorder = createTimeRecorderMetric(
                "object_query_save_time",
                "Time of query responsible for saving object",
                meterRegistry
            ),
            saveCounter = createCounterMetric(
                "object_query_save_count",
                "Number of executed queries responsible for saving object",
                meterRegistry
            ),
        )
}
