package com.dsniatecki.locationtracker.performer.locationsnapshot

import java.time.LocalDateTime
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

class LocationSnapshotMailCreator(
    private val templateEngine: TemplateEngine,
    private val templateName: String
) {

    fun create(
        effectiveAt: LocalDateTime,
        sentAt: LocalDateTime,
        locationSnapshots: Iterable<LocationSnapshot>
    ): String =
        templateEngine.process(templateName, createContext(effectiveAt, sentAt, locationSnapshots))

    private fun createContext(
        effectiveAt: LocalDateTime,
        sentAt: LocalDateTime,
        locationSnapshots: Iterable<LocationSnapshot>
    ): Context {
        val ctx = Context()
        ctx.setVariable("effectiveAt", formatTime(effectiveAt))
        ctx.setVariable("sentAt", formatTime(sentAt))
        ctx.setVariable("locationSnapshots", locationSnapshots)
        return ctx
    }

    private fun formatTime(sentAt: LocalDateTime): String = sentAt.toString().replace("T", " ")
}