package com.dsniatecki.locationtracker.receiver.config

import com.dsniatecki.locationtracker.commons.utils.TimeSupplier
import com.dsniatecki.locationtracker.commons.utils.createCounterMetric
import com.dsniatecki.locationtracker.receiver.objectlocation.ObjectLocationSender
import com.dsniatecki.locationtracker.receiver.objectlocation.ObjectLocationService
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.context.annotation.Bean

class ObjectLocationConfig {

    @Bean
    fun objectLocationService(
        objectLocationSender: ObjectLocationSender,
        timeSupplier: TimeSupplier,
        meterRegistry: MeterRegistry
    ): ObjectLocationService =
        ObjectLocationService(
            objectLocationSender = objectLocationSender,
            timeSupplier = timeSupplier,
            sentCounter = meterRegistry.createCounterMetric(
                "object_location_sent_count",
                "Number of sent object locations to rabbitMq"
            )
        )
}
