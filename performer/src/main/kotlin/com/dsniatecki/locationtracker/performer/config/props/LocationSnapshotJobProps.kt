package com.dsniatecki.locationtracker.performer.config.props

import com.dsniatecki.locationtracker.performer.sftp.SftpDestination
import java.time.Duration

data class LocationSnapshotJobProps(
    val schedulerCron: String,
    val tolerance: Duration,
    val sftp: SftpDestination,
    val objectIds: Set<String>,
)