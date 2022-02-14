package com.dsniatecki.locationtracker.receiver.objectlocation

import mu.KotlinLogging
import org.reactivestreams.Publisher
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import validate

@RestController
@RequestMapping(path = ["/api"])
class ObjectLocationController(
    private val objectLocationService: ObjectLocationService,
) {

    companion object {
        private val logger = KotlinLogging.logger { }
    }

    @PostMapping(value = ["/object-locations"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun saveObjectLocation(@RequestBody objectLocation: ObjectLocation): Publisher<Unit> {
        logger.debug { "Saving new object location: $objectLocation." }
        return Mono.just(objectLocation)
            .map { it.validate() }
            .flatMap { objectLocationService.save(it) }
            .onErrorMap({ it is IllegalStateException }) { ResponseStatusException(HttpStatus.BAD_REQUEST, it.message) }
            .onErrorMap({ it is IllegalArgumentException }) { ResponseStatusException(HttpStatus.BAD_REQUEST, it.message) }
    }
}