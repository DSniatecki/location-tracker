package com.dsniatecki.locationtracker.archiver.config

import com.dsniatecki.locationtracker.archiver.message.ObjectLocationProto
import com.dsniatecki.locationtracker.archiver.objectlocation.ObjectLocationRequestListener
import com.dsniatecki.locationtracker.archiver.objectlocation.ObjectLocationService
import com.dsniatecki.locationtracker.archiver.objectlocation.ObjectLocationSourceListener
import com.dsniatecki.locationtracker.commons.utils.createCounterMetric
import com.dsniatecki.locationtracker.commons.utils.createTimeRecorderMetric
import io.micrometer.core.instrument.MeterRegistry
import mu.KotlinLogging
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.MessageConversionException
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean

class AmqpConfig(
    @Value("\${spring.rabbitmq.object-location.source.queue}") private val objectLocationSourceQueueName: String,
    @Value("\${spring.rabbitmq.object-location.source.batch-size}") private val objectLocationSourceBatchSize: Int,
    @Value("\${spring.rabbitmq.object-location.source.concurrent-consumers}") private val objectLocationSourceConcurrentConsumers: Int,
    @Value("\${spring.rabbitmq.object-location.request.queue}") private val objectLocationRequestQueueName: String
) {
    companion object {
        private val logger = KotlinLogging.logger { }
    }

    class ObjectLocationSourceMessageConverter : MessageConverter {
        override fun toMessage(any: Any, messageProperties: MessageProperties): Message = throw NotImplementedError()

        override fun fromMessage(message: Message): Any =
            kotlin.runCatching {
                return ObjectLocationProto.ObjectLocations.newBuilder().mergeFrom(message.body).build()
            }.onFailure {
                logger.error(it) { "There was an error while trying to parse messages with object locations." }
                throw MessageConversionException("Unable to convert message with object locations", it)
            }
    }

    @Bean
    fun objectLocationSourceQueue(): Queue = Queue(objectLocationSourceQueueName)

    @Bean
    fun objectLocationRequestQueue(): Queue = Queue(objectLocationRequestQueueName)

    @Bean
    fun objectLocationSourceRabbitContainerFactory(connectionFactory: ConnectionFactory): SimpleRabbitListenerContainerFactory {
        val factory = SimpleRabbitListenerContainerFactory()
        factory.setConnectionFactory(connectionFactory)
        factory.setMessageConverter(ObjectLocationSourceMessageConverter())
        factory.setConcurrentConsumers(objectLocationSourceConcurrentConsumers)
        factory.setBatchSize(objectLocationSourceBatchSize)
        factory.setBatchListener(true)
        return factory
    }

    @Bean
    fun objectLocationRequestRabbitContainerFactory(connectionFactory: ConnectionFactory): SimpleRabbitListenerContainerFactory {
        val factory = SimpleRabbitListenerContainerFactory()
        factory.setConnectionFactory(connectionFactory)
        return factory
    }

    @Bean
    fun objectLocationSourceListener(
        objectLocationService: ObjectLocationService,
        meterRegistry: MeterRegistry
    ): ObjectLocationSourceListener =
        ObjectLocationSourceListener(
            objectLocationService = objectLocationService,
            timeRecorder = meterRegistry.createTimeRecorderMetric(
                "object_location_source_listener_time",
                "Time of saving object locations via rabbitMQ ObjectLocationSourceListener",
            ),
            savedCounter = meterRegistry.createCounterMetric(
                "object_location_source_listener_saved",
                "Number of saved object locations via rabbitMQ ObjectLocationSourceListener",
            ),
            errorCounter = meterRegistry.createCounterMetric(
                "object_location_source_listener_errors",
                "Number of errors that occurred in rabbitMQ ObjectLocationSourceListener",
            )
        )

    @Bean
    fun objectLocationRequestListener(
        objectLocationService: ObjectLocationService,
        rabbitTemplate: RabbitTemplate,
        meterRegistry: MeterRegistry
    ): ObjectLocationRequestListener =
        ObjectLocationRequestListener(
            objectLocationService = objectLocationService,
            rabbitTemplate = rabbitTemplate,
            timeRecorder = meterRegistry.createTimeRecorderMetric(
                "object_location_request_listener_time",
                "Time of requesting Object Locations via rabbitMQ ObjectLocationRequestListener"
            ),
            foundCounter = meterRegistry.createCounterMetric(
                "object_location_request_listener_found",
                "Number of requested Object Locations via rabbitMQ ObjectLocationRequestListener"
            ),
            errorCounter = meterRegistry.createCounterMetric(
                "object_location_request_listener_errors",
                "Number of errors that occurred in rabbitMQ ObjectLocationRequestListener"
            )
        )
}
