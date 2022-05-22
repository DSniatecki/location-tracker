package com.dsniatecki.locationtracker.receiver.objectlocation

import com.dsniatecki.locationtracker.receiver.api.internal.ObjectLocationsApi
import com.dsniatecki.locationtracker.receiver.model.internal.ObjectLocationDto
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestController
@RequestMapping(path = ["/api/internal"])
class ObjectLocationInternalController(private val objectLocationService: ObjectLocationService) : ObjectLocationsApi {

    companion object {
        private val logger = KotlinLogging.logger { }
    }

    override fun saveObjectLocation(
        objectLocationDto: Mono<ObjectLocationDto>,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<Void>> =
        objectLocationDto
            .doOnNext { logger.debug { "Saving new object location: $it." } }
            .flatMap { objectLocationService.save(it.toObjectLocation()) }
            .map { ResponseEntity.status(HttpStatus.CREATED).build() }
}