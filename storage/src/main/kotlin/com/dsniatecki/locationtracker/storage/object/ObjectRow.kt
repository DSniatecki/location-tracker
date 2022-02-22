package com.dsniatecki.locationtracker.storage.`object`

import java.time.LocalDateTime
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("object")
data class ObjectRow(
    @Id @Column("id") private val id: String,
    @Column("name") val name: String,
    @Column("image_url") val imageUrl: String?,
    @Column("created_at") val createdAt: LocalDateTime,
    @Column("updated_at") val updatedAt: LocalDateTime?,
    @Column("is_deleted") val isDeleted: Boolean,
) : Persistable<String> {
    override fun isNew() = updatedAt == null
    override fun getId(): String = id
}