package com.dsniatecki.locationtracker.receiver.config

import com.dsniatecki.locationtracker.receiver.objectlocation.ObjectLocationSender
import org.springframework.amqp.rabbit.batch.SimpleBatchingStrategy
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.BatchingRabbitTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

class AmqpConfig(
    @Value("\${spring.rabbitmq.object-location.exchange}") private val exchange: String,
    @Value("\${spring.rabbitmq.object-location.store.routing-key}") private val storeRoutingKey: String,
    @Value("\${spring.rabbitmq.object-location.store.pool-size}") private val storePoolSize: Int,
    @Value("\${spring.rabbitmq.object-location.store.batch-size}") private val storeBatchSize: Int,
    @Value("\${spring.rabbitmq.object-location.store.buffer-limit}") private val storeBufferLimit: Int,
    @Value("\${spring.rabbitmq.object-location.store.timeout-millis}") private val storeTimeoutMillis: Long
) {

    @Bean
    fun objectLocationStoreTaskScheduler(): TaskScheduler {
        val taskScheduler = ThreadPoolTaskScheduler()
        taskScheduler.poolSize = storePoolSize
        return taskScheduler
    }

    @Bean
    fun objectLocationStoreBatchingRabbitTemplate(
        connectionFactory: ConnectionFactory,
        objectLocationStoreTaskScheduler: TaskScheduler
    ): BatchingRabbitTemplate {
        val strategy = SimpleBatchingStrategy(storeBatchSize, storeBufferLimit, storeTimeoutMillis)
        val template = BatchingRabbitTemplate(strategy, objectLocationStoreTaskScheduler)
        template.connectionFactory = connectionFactory
        return template
    }

    @Bean
    fun objectLocationSender(
        objectLocationStoreBatchingRabbitTemplate: BatchingRabbitTemplate
    ): ObjectLocationSender =
        ObjectLocationSender(
            rabbitTemplate = objectLocationStoreBatchingRabbitTemplate,
            exchange = exchange,
            routingKey = storeRoutingKey
        )
}
