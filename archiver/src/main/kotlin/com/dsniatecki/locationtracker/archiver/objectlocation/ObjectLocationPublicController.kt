package com.dsniatecki.locationtracker.archiver.objectlocation

import com.dsniatecki.locationtracker.archiver.api.pub.ObjectLocationsApi
import com.dsniatecki.locationtracker.archiver.model.pub.ObjectLocationDto
import com.dsniatecki.locationtracker.commons.utils.TimeSupplier
import java.time.Duration
import java.time.LocalDateTime
import java.util.Optional
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux

@RestController
@RequestMapping(path = ["/api/public"])
class ObjectLocationPublicController(
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
        logger.debug {
            "Returning effective object location for object id: $objectId, effective at: " +
                "'$searchEffectiveAt' and tolerance: $tolerance seconds ."
        }
        return objectLocationService.getEffectiveAt(objectId, searchEffectiveAt, toleranceDuration)
            .switchIfEmpty(Mono.error(NoSuchElementException("Effective object locations for object id: $objectId, " +
                "effective at: '$effectiveAt' and tolerance: $tolerance seconds does not exist.")))
            .map { ResponseEntity.ok(it.toPublicDto()) }
    }

    override fun getEffectiveObjectLocations(
        objectIds: Set<String>,
        effectiveAt: Optional<LocalDateTime>,
        tolerance: Optional<Long>,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<Flux<ObjectLocationDto>>> {
        val toleranceDuration = tolerance.map { Duration.ofSeconds(it) }.orElse(null)
        val searchEffectiveAt = effectiveAt.orElseGet { timeSupplier.now() }
        logger.debug {
            "Returning effective object locations for object ids: $objectIds, effective at: " +
                "'$searchEffectiveAt' and tolerance: $tolerance seconds ."
        }
        return Mono.just(
            objectIds.toFlux()
                .flatMap { objectLocationService.getEffectiveAt(it, searchEffectiveAt, toleranceDuration) }
                .map { it.toPublicDto() }
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