package com.dsniatecki.locationtracker.performer.config

import com.dsniatecki.locationtracker.archiver.ApiClient as ArchiverApiClient
import com.dsniatecki.locationtracker.storage.ApiClient as StorageApiClient
import com.dsniatecki.locationtracker.commons.utils.TimeSupplier
import com.dsniatecki.locationtracker.performer.sftp.SftpSender
import com.fasterxml.jackson.databind.ObjectMapper
import java.text.DateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import net.schmizz.sshj.DefaultConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean

class UtilsConfig(
    @Value("\${performer.time-zone-id}") private val timeZoneId: String,
    @Value("\${performer.storage.base-path}") private val storageBasePath: String,
    @Value("\${performer.archiver.base-path}") private val archiverBasePath: String
) {

    private val zoneId = ZoneId.of(timeZoneId)

    @Bean
    fun timeSupplier(): TimeSupplier = object : TimeSupplier {
        override fun now(): LocalDateTime = LocalDateTime.now(zoneId)
    }

    @Bean
    fun sftpSender(): SftpSender = SftpSender(DefaultConfig())

    @Bean
    fun storageApiClient(objectMapper: ObjectMapper): StorageApiClient {
        val apiClient = StorageApiClient(objectMapper, DateFormat.getDateInstance())
        apiClient.basePath = storageBasePath
        return apiClient
    }

    @Bean
    fun archiverApiClient(objectMapper: ObjectMapper): ArchiverApiClient {
        val apiClient = ArchiverApiClient(objectMapper, DateFormat.getDateInstance())
        apiClient.basePath = archiverBasePath
        return apiClient
    }
}
