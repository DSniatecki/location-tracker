package com.dsniatecki.locationtracker.storage.`object`

import java.time.LocalDateTime
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ObjectRowRepository : ReactiveCrudRepository<ObjectRow, String> {

    @Query("SELECT * FROM object WHERE id = :id AND is_deleted = FALSE")
    override fun findById(id: String): Mono<ObjectRow>

    @Query("SELECT * FROM object WHERE is_deleted = FALSE")
    override fun findAll(): Flux<ObjectRow>

    @Query("UPDATE object SET updated_at = :deleted_at, is_deleted = true WHERE id = :id")
    fun delete(id: String, deleted_at: LocalDateTime): Mono<Unit>
}