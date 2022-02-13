package com.dsniatecki.locationtracker.receiver

const val RABBITMQ_CONTAINER = "rabbitmq:3.9.8-management-alpine"

const val RABBITMQ_DEFINITIONS_PATH = "rabbitmq-test-definitions.json"

const val RABBITMQ_USERNAME = "archiver"
const val RABBITMQ_PASSWORD = "archiver"
const val RABBITMQ_VIRTUAL_HOST = "location-tracker"

const val OBJECT_LOCATION_EXCHANGE = "archiver.exchange"
const val OBJECT_LOCATION_SOURCE_ROUTING_KEY = "archiver-object-location-source"
const val OBJECT_LOCATION_SOURCE_QUEUE = "archiver.object.location.source.queue"
