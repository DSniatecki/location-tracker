package com.dsniatecki.locationtracker.performer.sftp

data class SftpDestination(
    val host: String,
    val port: Int,
    val user: String,
    val password: String,
    val path: String
)