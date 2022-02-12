package com.dsniatecki.locationtracker.archiver.objectlocation

import com.dsniatecki.locationtracker.archiver.message.ObjectLocationProto
import com.dsniatecki.locationtracker.archiver.utils.Counter
import com.dsniatecki.locationtracker.archiver.utils.TimeRecorder
import com.dsniatecki.locationtracker.archiver.utils.recorded
import com.rabbitmq.client.Channel
import mu.KotlinLogging
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.support.AmqpHeaders
import org.springframework.messaging.handler.annotation.Header
import reactor.core.publisher.Mono

data class ObjectLocationSourceListener(
    private val objectLocationService: ObjectLocationService,
    private val timeRecorder: TimeRecorder,
    private val savedCounter: Counter,
    private val errorCounter: Counter
) {

    companion object {
        private val logger = KotlinLogging.logger { }
    }

    @RabbitListener(
        queues = ["#{objectLocationSourceQueue.name}"],
        containerFactory = "objectLocationSourceRabbitContainerFactory",
        ackMode = "MANUAL"
    )
    fun receive(
        objectLocationsProtos: List<ObjectLocationProto.ObjectLocations>,
        channel: Channel,
        @Header(AmqpHeaders.DELIVERY_TAG) tag: Long
    ) {
        logger.debug { "New batch of ${objectLocationsProtos.size} messages with object locations received." }
        Mono.just(objectLocationsProtos)
            .map { mapToObjectLocations(it) }
            .doOnError { logger.error(it) { "There was an error while trying to parse messages with object locations." } }
            .flatMap { ratesArchives ->
                objectLocationService.saveAll(ratesArchives)
                    .doOnSuccess { savedCounter.add(ratesArchives.count()) }
            }.doOnSuccess {
                channel.basicAck(tag, false)
                logger.debug { "Received object locations were successfully saved." }
            }.doOnError {
                channel.basicReject(tag, false)
                logger.error(it) { "There was an error while trying to save object locations." }
                errorCounter.increment()
            }.onErrorResume { Mono.empty() }
            .recorded(timeRecorder)
            .subscribe()
    }

    private fun mapToObjectLocations(objectLocationsProtos: Iterable<ObjectLocationProto.ObjectLocations>): Iterable<ObjectLocation> =
        objectLocationsProtos.map { it.objectLocationsList }
            .flatten()
            .map { it.toObjectLocation() }
}