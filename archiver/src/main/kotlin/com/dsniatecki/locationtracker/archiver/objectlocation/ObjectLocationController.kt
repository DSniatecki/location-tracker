package com.dsniatecki.locationtracker.archiver.objectlocation

import com.dsniatecki.locationtracker.commons.utils.TimeSupplier
import java.time.Duration
import java.time.LocalDateTime
import mu.KotlinLogging
import org.reactivestreams.Publisher
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux

@RestController
@RequestMapping(path = ["/api/public", "/api/internal"])
class ObjectLocationController(
    private val objectLocationService: ObjectLocationService,
    private val timeSupplier: TimeSupplier,
) {

    companion object {
        private val logger = KotlinLogging.logger { }
    }

    @GetMapping(value = ["/object-locations/{objectId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getEffectiveObjectLocation(
        @PathVariable(name = "objectId") objectId: String,
        @RequestParam(name = "effectiveAt", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) effectiveAt: LocalDateTime?,
        @RequestParam(name = "tolerance", required = false) tolerance: Long?,
    ): Publisher<ObjectLocation> {
        val toleranceDuration = tolerance?.let { Duration.ofSeconds(it) }
        val searchEffectiveAt  = effectiveAt ?: timeSupplier.now()
        logger.debug { "Returning effective object location for object id: $objectId, effective at: " +
            "'$searchEffectiveAt' and tolerance: $tolerance seconds ." }
        return objectLocationService.getEffectiveAt(objectId, searchEffectiveAt, toleranceDuration)
            .switchIfEmpty(Mono.error(NoSuchElementException("Effective object locations for object id: $objectId, " +
                "effective at: '$effectiveAt' and tolerance: $tolerance seconds does not exist.")))
            .onErrorMap({ it is NoSuchElementException }) { ResponseStatusException(HttpStatus.NOT_FOUND, it.message) }
    }

    @GetMapping(value = ["/object-locations"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getEffectiveObjectLocations(
        @RequestParam(name = "objectIds") objectIds: Set<String>,
        @RequestParam(name = "effectiveAt", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) effectiveAt: LocalDateTime?,
        @RequestParam(name = "tolerance", required = false) tolerance: Long?,
    ): Publisher<ObjectLocation> {
        val toleranceDuration = tolerance?.let { Duration.ofSeconds(it) }
        val searchEffectiveAt = effectiveAt ?: timeSupplier.now()
        logger.debug { "Returning effective object locations for object ids: $objectIds, effective at: " +
                "'$searchEffectiveAt' and tolerance: $tolerance seconds ." }
        return objectIds.toFlux()
            .flatMap { objectLocationService.getEffectiveAt(it, searchEffectiveAt, toleranceDuration) }
            .sort(Comparator.comparing { it.objectId })
    }

    @PostMapping(value = ["/object-locations"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun saveObjectLocations(@RequestBody objectLocations: Iterable<ObjectLocation>): Publisher<Unit> {
        logger.debug { "Saving new ${objectLocations.count()} object locations." }
        return Mono.just(objectLocations)
            .map { objectLocations.map { it.validate() } }
            .flatMap { objectLocationService.saveAll(it) }
            .onErrorMap({ it is IllegalStateException }) { ResponseStatusException(HttpStatus.BAD_REQUEST, it.message) }
            .onErrorMap({ it is IllegalArgumentException }) { ResponseStatusException(HttpStatus.BAD_REQUEST, it.message) }
            .onErrorMap({ it is DataIntegrityViolationException }) { ResponseStatusException(HttpStatus.CONFLICT, it.message) }
    }
}