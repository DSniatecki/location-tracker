package com.dsniatecki.locationtracker.performer.sftp

import java.io.InputStream
import net.schmizz.sshj.xfer.InMemorySourceFile

class InMemoryTextFile(
    private val name: String,
    private val text: String
) : InMemorySourceFile() {

    override fun getName(): String = name

    override fun getInputStream(): InputStream = text.byteInputStream()

    override fun getLength(): Long = text.length.toLong()
}