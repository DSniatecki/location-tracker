package com.dsniatecki.locationtracker.performer.config

import com.dsniatecki.locationtracker.archiver.ApiClient
import com.dsniatecki.locationtracker.archiver.api.ObjectLocationControllerApi
import com.dsniatecki.locationtracker.commons.utils.TimeSupplier
import com.dsniatecki.locationtracker.commons.utils.atZone
import com.fasterxml.jackson.databind.ObjectMapper
import java.text.DateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean

class UtilsConfig(
    @Value("\${performer.time-zone.id}") private val timeZoneId: String,
    @Value("\${performer.time-zone.offset}") private val timeZoneOffset: Int,
    @Value("\${performer.storage.base-path}") private val storageBasePath: String,
    @Value("\${performer.archiver.base-path}") private val archiverBasePath: String
) {

    private val zoneId = ZoneId.of(timeZoneId)
    private val zoneOffset = ZoneOffset.ofHours(timeZoneOffset)

    @Bean
    fun timeSupplier(): TimeSupplier = object : TimeSupplier {
        override fun now(): LocalDateTime = LocalDateTime.now(zoneId)
        override fun zoneId(): ZoneId = zoneId
        override fun zoneOffset(): ZoneOffset = zoneOffset
    }

    @Bean
    fun archiverApiClient(objectMapper: ObjectMapper): ApiClient {
        val apiClient = ApiClient(objectMapper, DateFormat.getDateInstance())
        apiClient.basePath = archiverBasePath
        return apiClient
    }

    @Bean
    fun storageApiClient(objectMapper: ObjectMapper): ApiClient {
        val apiClient = ApiClient(objectMapper, DateFormat.getDateInstance())
        apiClient.basePath = archiverBasePath
        return apiClient
    }

//    @Bean
//    fun objectLocationControllerApi(archiverApiClient: ApiClient): ObjectLocationControllerApi {
//        val objectLocationControllerApi = ObjectLocationControllerApi(archiverApiClient)
//        val a = objectLocationControllerApi.getEffectiveObjectLocation("c039e9fc-8b46-11ec-a8a3-0242ac120002", LocalDateTime.now().atOffset(ZoneOffset.UTC), Duration.ofDays(1).toSeconds())
//
//        val objectLocation = a.block()
//        println(objectLocation)
//        println(objectLocation)
//        println(objectLocation)
//
//        return objectLocationControllerApi
//    }


//    @Bean
//    fun objectLocationControllerApi2(storageApiClient: ApiClient): ObjectLocationControllerApi {
//        val objectLocationControllerApi = ObjectLocationControllerApi(archiverApiClient)
//        val a = objectLocationControllerApi.getEffectiveObjectLocation("c039e9fc-8b46-11ec-a8a3-0242ac120002", LocalDateTime.now().atOffset(ZoneOffset.UTC), Duration.ofDays(1).toSeconds())
//
//        val objectLocation = a.block()
//        println(objectLocation)
//        println(objectLocation)
//        println(objectLocation)
//
//        return objectLocationControllerApi
//    }

}
