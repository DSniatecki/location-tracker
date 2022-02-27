package com.dsniatecki.locationtracker.performer.locationsnapshot

import com.dsniatecki.locationtracker.performer.sftp.InMemoryTextFile
import com.dsniatecki.locationtracker.performer.sftp.SftpDestination
import com.dsniatecki.locationtracker.performer.sftp.SftpSender
import com.fasterxml.jackson.databind.ObjectMapper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import mu.KotlinLogging

class LocationSnapshotSender(
    private val sftpSender: SftpSender,
    private val sftpDestination: SftpDestination,
    private val objectMapper: ObjectMapper
) {

    companion object {
        private val dateTimePattern = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")
        private val logger = KotlinLogging.logger { }
    }

    fun send(locationSnapshots: Iterable<LocationSnapshot>, effectiveAt: LocalDateTime) {
        val fileName = createFileName(effectiveAt)
        val locationSnapshotsFile = InMemoryTextFile(fileName, mapToJson(locationSnapshots))
        logger.debug { "Sending file: '$fileName' containing location snapshots to sftp server: '${sftpDestination.host}' ... " }
        sftpSender.send(sftpDestination, locationSnapshotsFile)
        logger.info { "File: '$fileName' containing location snapshots was sent to sftp server: '${sftpDestination.host}'" }
    }

    private fun mapToJson(locationSnapshots: Iterable<LocationSnapshot>): String =
        objectMapper.writeValueAsString(locationSnapshots.sortedBy { it.objectId })

    private fun createFileName(effectiveAt: LocalDateTime): String =
        "location-snapshots_${effectiveAt.format(dateTimePattern)}.json"
}