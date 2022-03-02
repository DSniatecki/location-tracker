package com.dsniatecki.locationtracker.storage.config

import com.dsniatecki.locationtracker.commons.utils.TimeSupplier
import com.dsniatecki.locationtracker.commons.utils.createTimeRecorderMetric
import com.dsniatecki.locationtracker.commons.utils.withCounter
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
            findTimeRecorder = meterRegistry.createTimeRecorderMetric(
                "object_query_find_time",
                "Time of query responsible for finding object by id"
            ).withCounter(
                "object_query_find_count",
                "Number of executed queries responsible for finding object by id"
            ),
            findMultipleTimeRecorder = meterRegistry.createTimeRecorderMetric(
                "object_query_find_multiple_time",
                "Time of query responsible for finding multiple objects by ids"
            ).withCounter(
                "object_query_find_multiple_count",
                "Number of executed queries responsible for finding all objects"
            ),
            findAllTimeRecorder = meterRegistry.createTimeRecorderMetric(
                "object_query_find_all_time",
                "Time of query responsible for finding multiple objects by ids"
            ).withCounter(
                "object_query_find_all_count",
                "Number of executed queries responsible for finding all objects"
            ),
            saveTimeRecorder = meterRegistry.createTimeRecorderMetric(
                "object_query_save_time",
                "Time of query responsible for saving object",
            ).withCounter(
                "object_query_save_count",
                "Number of executed queries responsible for saving object"
            ),
            deleteTimeRecorder = meterRegistry.createTimeRecorderMetric(
                "object_query_delete_time",
                "Time of query responsible for deleting object"
            ).withCounter(
                "object_query_delete_count",
                "Number of executed queries responsible for deleting object"
            )
        )
}
