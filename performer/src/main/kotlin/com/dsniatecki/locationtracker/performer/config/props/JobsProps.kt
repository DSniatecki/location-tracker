package com.dsniatecki.locationtracker.performer.config.props

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "performer.jobs")
data class JobsProps(
    val locationSnapshot: LocationSnapshotJobProps
)