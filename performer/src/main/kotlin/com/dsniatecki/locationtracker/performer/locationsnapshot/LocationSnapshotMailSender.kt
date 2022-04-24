package com.dsniatecki.locationtracker.performer.locationsnapshot

import com.dsniatecki.locationtracker.commons.utils.TimeSupplier
import com.dsniatecki.locationtracker.performer.config.props.LocationSnapshotJobMailProps
import java.time.LocalDateTime
import javax.mail.Address
import javax.mail.Message
import javax.mail.Multipart
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import org.springframework.mail.MailMessage
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMailMessage

class LocationSnapshotMailSender(
    private val javaMailSender: JavaMailSender,
    private val timeSupplier: TimeSupplier,
    private val mailProps: LocationSnapshotJobMailProps
) {
    private val mailFormat = "text/html; charset=utf-8"
    private val recipientAddresses = mailProps.recipients.map { InternetAddress(it) }.toTypedArray()

    fun send(mailBody: String) {
        javaMailSender.send(createMsg(mailBody))
    }

    private fun createMsg(mailBody: String): MimeMessage {
        val msg = javaMailSender.createMimeMessage()
        msg.setFrom(mailProps.sender)
        msg.setRecipients(Message.RecipientType.TO, recipientAddresses)
        msg.subject = createSubject()
        msg.setContent(mailBody, mailFormat)
        return msg
    }

    private fun createSubject(): String =
        mailProps.subjectTemplate.replace("{{time}}", timeSupplier.now().toString().replace("T", " "))
}