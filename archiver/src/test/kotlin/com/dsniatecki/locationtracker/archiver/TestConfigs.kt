package com.dsniatecki.locationtracker.archiver

const val TIMEOUT_VALUE_MILLIS = 5000L

const val DB_CONTAINER = "timescale/timescaledb:2.5.1-pg14"
const val RABBITMQ_CONTAINER = "rabbitmq:3.9.8-management-alpine"

const val OBJECT_LOCATION_TABLE = "object_location"

const val RABBITMQ_DEFINITIONS_PATH = "rabbitmq-test-definitions.json"

const val RABBITMQ_USERNAME = "archiver"
const val RABBITMQ_PASSWORD = "archiver"
const val RABBITMQ_VIRTUAL_HOST = "location-tracker"
const val RABBITMQ_EXCHANGE = "archiver.exchange"

const val OBJECT_LOCATION_SOURCE_QUEUE = "archiver.object.location.source.queue"
const val OBJECT_LOCATION_REQUEST_QUEUE = "archiver.object.location.request.queue"
const val DEAD_LETTER_QUEUE = "archiver.dead.letter.queue"

const val OBJECT_LOCATION_SOURCE_ROUTING_KEY = "archiver-object-location-source"
const val OBJECT_LOCATION_REQUEST_ROUTING_KEY = "archiver-object-location-request"
