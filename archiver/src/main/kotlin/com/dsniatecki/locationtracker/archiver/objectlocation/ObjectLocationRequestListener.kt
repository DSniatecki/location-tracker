package com.dsniatecki.locationtracker.archiver.objectlocation

import com.dsniatecki.locationtracker.archiver.message.Common
import com.dsniatecki.locationtracker.archiver.message.ObjectLocationProto
import com.dsniatecki.locationtracker.archiver.message.utils.toUtcOffsetDateTime
import com.dsniatecki.locationtracker.archiver.utils.Counter
import com.dsniatecki.locationtracker.archiver.utils.TimeRecorder
import com.dsniatecki.locationtracker.archiver.utils.recorded
import com.rabbitmq.client.Channel
import java.time.Duration
import mu.KotlinLogging
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.AmqpHeaders
import org.springframework.messaging.handler.annotation.Header
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux

data class ObjectLocationRequestListener(
    private val objectLocationService: ObjectLocationService,
    private val rabbitTemplate: RabbitTemplate,
    private val timeRecorder: TimeRecorder,
    private val foundCounter: Counter,
    private val errorCounter: Counter
) {
    companion object {
        private val logger = KotlinLogging.logger { }
    }

    @RabbitListener(
        queues = ["#{objectLocationRequestQueue.name}"],
        containerFactory = "objectLocationRequestRabbitContainerFactory",
        ackMode = "MANUAL"
    )
    fun receive(message: Message, channel: Channel, @Header(AmqpHeaders.DELIVERY_TAG) tag: Long) {
        Mono.just(message)
            .map { parseMessage(it) }
            .doOnSuccess { logger.debug { "New ${it.requestsList.size} requests for object locations received." } }
            .doOnError { logger.error(it) { "There was an error while trying to parse message with requests for object locations." } }
            .flatMap { getObjectLocationsForRequests(it) }
            .map { ObjectLocationProto.ObjectLocationRequestsResponse.newBuilder().setObjectLocations(it).build() }
            .doOnSuccess {
                replyWithMessage(channel, tag, it.toByteArray(), message.messageProperties)
                logger.debug { "Object locations were successfully found." }
                foundCounter.add(it.objectLocations.objectLocationsCount)
            }
            .doOnError {
                replyWithMessage(channel, tag, createErrorRequestsResponse(it).toByteArray(), message.messageProperties)
                logger.error(it) { "There was an error while trying to find object locations." }
                errorCounter.increment()
            }
            .recorded(timeRecorder)
            .subscribe()
    }

    private fun replyWithMessage(
        channel: Channel,
        tag: Long,
        body: ByteArray,
        receivedMessageProperties: MessageProperties
    ) {
        val properties = MessageProperties()
        properties.correlationId = receivedMessageProperties.correlationId
        kotlin.runCatching {
            rabbitTemplate.send(receivedMessageProperties.replyTo, Message(body, properties))
        }.onSuccess {
            channel.basicAck(tag, false)
        }.onFailure {
            channel.basicReject(tag, false)
            logger.error(it) { "There was an error while trying to reply to rabbitMq with object locations requests response." }
        }
    }

    private fun parseMessage(message: Message): ObjectLocationProto.ObjectLocationRequests =
        ObjectLocationProto.ObjectLocationRequests.newBuilder().mergeFrom(message.body).build()

    private fun getObjectLocationsForRequests(proto: ObjectLocationProto.ObjectLocationRequests): Mono<ObjectLocationProto.ObjectLocations> =
        proto.requestsList.toFlux()
            .flatMap { objectLocationService.getEffectiveAt(it.objectId, it.effectiveAt.toUtcOffsetDateTime(), Duration.ofSeconds(it.tolerance.toBigDecimal().toLong())) }
            .sort(Comparator.comparing<ObjectLocation?, String?> { it.objectId }
                .thenComparing(Comparator.comparing { it.receivedAt })
            ).map { it.toProto() }
            .collectList()
            .map { ObjectLocationProto.ObjectLocations.newBuilder().addAllObjectLocations(it).build() }

    private fun createErrorRequestsResponse(error: Throwable): ObjectLocationProto.ObjectLocationRequestsResponse =
        ObjectLocationProto.ObjectLocationRequestsResponse.newBuilder()
            .setErrorResponse(
                Common.ErrorResponse.newBuilder()
                    .setErrorMessage("There was an error while requesting object locations: $error. Error msg: ${error.message}")
                    .build()
            ).build()
}
