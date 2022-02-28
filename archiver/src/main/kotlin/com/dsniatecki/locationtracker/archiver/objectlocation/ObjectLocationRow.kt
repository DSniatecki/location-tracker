package com.dsniatecki.locationtracker.archiver.objectlocation

import java.math.BigDecimal
import java.time.LocalDateTime
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("object_location")
data class ObjectLocationRow(
    @Id @Column("id") val id: Long = 0,
    @Column("object_id") val objectId: String,
    @Column("received_at") val receivedAt: LocalDateTime,
    @Column("latitude") val latitude: BigDecimal,
    @Column("longitude") val longitude: BigDecimal
)
