package com.dsniatecki.locationtracker.archiver.objectlocation

import com.dsniatecki.locationtracker.commons.utils.Counter
import com.dsniatecki.locationtracker.commons.utils.TimeRecorder
import com.dsniatecki.locationtracker.commons.utils.recorded
import java.time.Duration
import java.time.LocalDateTime
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter
import org.springframework.r2dbc.core.DatabaseClient
import reactor.core.publisher.Mono

class ObjectLocationRepository(
    private val databaseClient: DatabaseClient,
    private val converter: MappingR2dbcConverter,
    private val findTimeRecorder: TimeRecorder,
    private val findCounter: Counter,
    private val saveTimeRecorder: TimeRecorder,
    private val saveCounter: Counter
) {

    fun findEffectiveAt(objectId: String, effectiveAt: LocalDateTime, tolerance: Duration): Mono<ObjectLocationRow> =
        databaseClient.sql(
            """
            SELECT *
            FROM object_location
            WHERE received_at = (
                SELECT MAX(received_at)
                FROM object_location
                WHERE object_id = :objectId AND received_at >= :searchStartsAt AND received_at <= :effectiveAt
            ) AND object_id = :objectId
            ORDER BY id DESC
            LIMIT 1
        """.trimIndent()
        )
            .bind("objectId", objectId)
            .bind("searchStartsAt", resolveSearchStartsAt(effectiveAt, tolerance))
            .bind("effectiveAt", effectiveAt)
            .map { row, metadata -> converter.read(ObjectLocationRow::class.java, row, metadata) }
            .one()
            .doOnEach { findCounter.increment() }
            .recorded(findTimeRecorder)

    fun saveAll(objectLocationRows: Iterable<ObjectLocationRow>): Mono<Unit> =
        if (objectLocationRows.count() > 0) saveElements(objectLocationRows) else Mono.empty()

    private fun resolveSearchStartsAt(at: LocalDateTime, tolerance: Duration): LocalDateTime =
        at.minusSeconds(tolerance.toSeconds())

    private fun saveElements(objectLocationRows: Iterable<ObjectLocationRow>): Mono<Unit> =
        databaseClient.sql(createInsertSqlQuery(objectLocationRows))
            .fetch()
            .one()
            .flatMap { Mono.empty<Unit>() }
            .doOnEach { saveCounter.increment() }
            .recorded(saveTimeRecorder)

    private fun createInsertSqlQuery(objectLocationRows: Iterable<ObjectLocationRow>): String {
        val sqlBuilder = StringBuilder("INSERT INTO object_location(object_id, received_at, latitude, longitude) VALUES ")
        objectLocationRows.forEach {
            sqlBuilder.append("('${it.objectId}','${it.receivedAt}',${it.latitude},${it.longitude}),")
        }
        removeLastSignIfComma(sqlBuilder)
        return sqlBuilder.append(";").toString()
    }

    private fun removeLastSignIfComma(sqlBuilder: StringBuilder) {
        if (sqlBuilder.endsWith(",")) {
            sqlBuilder.replace(sqlBuilder.length - 1, sqlBuilder.length, "")
        }
    }
}
