package com.dsniatecki.locationtracker.performer.config.props

data class LocationSnapshotJobMailProps(
    val template: String,
    val subjectTemplate: String,
    val sender: String,
    val recipients: Set<String>
)