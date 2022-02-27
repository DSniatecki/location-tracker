package com.dsniatecki.locationtracker.performer.sftp

import net.schmizz.sshj.Config
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.transport.verification.PromiscuousVerifier
import net.schmizz.sshj.xfer.LocalSourceFile

class SftpSender(
    private val clientConfig: Config
) {
    companion object {
        private const val DIRECTORY_SEPARATOR = "/"
    }

    fun send(sftpDestination: SftpDestination, file: LocalSourceFile) {
        createSshClient(sftpDestination).use {
            it.newSFTPClient().use { client -> client.put(file, createFileDestination(sftpDestination, file)) }
        }
    }

    private fun createFileDestination(sftpDestination: SftpDestination, file: LocalSourceFile) =
        if (sftpDestination.path.endsWith(DIRECTORY_SEPARATOR))
            sftpDestination.path + file.name
        else
            sftpDestination.path + DIRECTORY_SEPARATOR + file.name

    private fun createSshClient(sftpDestination: SftpDestination): SSHClient {
        val sshClient = SSHClient(clientConfig)
        sshClient.addHostKeyVerifier(PromiscuousVerifier())
        sshClient.connect(sftpDestination.host, sftpDestination.port)
        sshClient.authPassword(sftpDestination.user, sftpDestination.password)
        return sshClient
    }
}