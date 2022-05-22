package com.dsniatecki.locationtracker.receiver.objectlocation

import com.dsniatecki.locationtracker.archiver.message.ObjectLocationProto
import com.dsniatecki.locationtracker.archiver.message.utils.toProtoDateTime
import com.dsniatecki.locationtracker.archiver.message.utils.toProtoDecimal
import java.time.LocalDateTime
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.core.RabbitTemplate

class ObjectLocationSender(
    private val rabbitTemplate: RabbitTemplate,
    private val exchange: String,
    private val routingKey: String
) {

    fun send(objectLocation: ObjectLocation, receivedAt: LocalDateTime) {
        val objectLocationsProto = createProto(objectLocation, receivedAt)
        rabbitTemplate.send(exchange, routingKey, Message(objectLocationsProto.toByteArray()))
    }

    private fun createProto(
        objectLocation: ObjectLocation,
        receivedAt: LocalDateTime
    ): ObjectLocationProto.ObjectLocations =
        ObjectLocationProto.ObjectLocations.newBuilder()
            .addObjectLocations(
                ObjectLocationProto.ObjectLocation.newBuilder()
                    .setObjectId(objectLocation.objectId)
                    .setReceivedAt(receivedAt.toProtoDateTime())
                    .setLatitude(objectLocation.latitude.toProtoDecimal())
                    .setLongitude(objectLocation.longitude.toProtoDecimal())
                    .build()
            )
            .build()
}