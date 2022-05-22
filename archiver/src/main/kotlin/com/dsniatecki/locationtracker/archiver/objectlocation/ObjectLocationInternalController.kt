package com.dsniatecki.locationtracker.archiver.objectlocation

import com.dsniatecki.locationtracker.archiver.api.internal.ObjectLocationsApi
import com.dsniatecki.locationtracker.archiver.model.internal.ObjectLocationDto
import com.dsniatecki.locationtracker.commons.utils.TimeSupplier
import java.time.Duration
import java.time.LocalDateTime
import java.util.Optional
import mu.KotlinLogging
import org.reactivestreams.Publisher
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono

@RestController
@RequestMapping(path = ["/api/internal"])
class ObjectLocationInternalController(
    private val objectLocationService: ObjectLocationService,
    private val timeSupplier: TimeSupplier,
) : ObjectLocationsApi {

    companion object {
        private val logger = KotlinLogging.logger { }
    }

    override fun getEffectiveObjectLocation(
        objectId: String,
        effectiveAt: Optional<LocalDateTime>,
        tolerance: Optional<Long>,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<ObjectLocationDto>> {
        val toleranceDuration = tolerance.map { Duration.ofSeconds(it) }.orElse(null)
        val searchEffectiveAt = effectiveAt.orElseGet { timeSupplier.now() }
        logger.debug { "Returning effective object location for object id: $objectId, effective at: " +
                "'$searchEffectiveAt' and tolerance: $tolerance seconds ." }
        return objectLocationService.getEffectiveAt(objectId, searchEffectiveAt, toleranceDuration)
            .switchIfEmpty(Mono.error(NoSuchElementException("Effective object locations for object id: $objectId, " +
                "effective at: '$effectiveAt' and tolerance: $tolerance seconds does not exist.")))
            .map { ResponseEntity.ok(it.toInternalDto()) }
    }

    override fun getEffectiveObjectLocations(
        objectIds: Set<String>,
        effectiveAt: Optional<LocalDateTime>,
        tolerance: Optional<Long>,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<Flux<ObjectLocationDto>>> {
        val toleranceDuration = tolerance.map { Duration.ofSeconds(it) }.orElse(null)
        val searchEffectiveAt = effectiveAt.orElseGet { timeSupplier.now() }
        logger.debug { "Returning effective object locations for object ids: $objectIds, effective at: " +
                "'$searchEffectiveAt' and tolerance: $tolerance seconds ." }
        return Mono.just(
            objectIds.toFlux()
                .flatMap { objectLocationService.getEffectiveAt(it, searchEffectiveAt, toleranceDuration) }
                .map { it.toInternalDto() }
                .sort(Comparator.comparing { it.objectId })
        ).map { ResponseEntity.ok(it) }
    }

    override fun saveObjectLocations(
        objectLocationDto: Flux<ObjectLocationDto>,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<Void>> =
        objectLocationDto
            .map { it.toObjectLocation() }
            .collectList()
            .doOnNext { logger.debug { "Saving new ${it.count()} object locations." } }
            .flatMap { objectLocationService.saveAll(it) }
            .map { ResponseEntity.status(HttpStatus.CREATED).build() }
}