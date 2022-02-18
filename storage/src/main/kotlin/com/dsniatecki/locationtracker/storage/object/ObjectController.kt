package com.dsniatecki.locationtracker.storage.`object`

import org.reactivestreams.Publisher
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono

@RestController
@RequestMapping(path = ["/api"])
class ObjectController(
    private val objectService: ObjectService,
) {

    @GetMapping(value = ["/objects/{objectId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getObject(@PathVariable(name = "objectId") objectId: String): Publisher<ExistingObject> =
        objectService.get(objectId)
            .switchIfEmpty(Mono.error(NoSuchElementException("Object with id: '$objectId' does not exist.")))
            .onErrorMap({ it is NoSuchElementException }) { ResponseStatusException(HttpStatus.NOT_FOUND, it.message) }

    @GetMapping(value = ["/objects"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAllObjects(): Publisher<ExistingObject> = objectService.getAll()

    @PostMapping(value = ["/objects"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun saveObject(@RequestBody newObject: NewObject): Publisher<ExistingObject> =
        Mono.just(newObject)
            .map { it.validate() }
            .flatMap { objectService.save(newObject) }
            .onErrorMap({ it is IllegalStateException }) { ResponseStatusException(HttpStatus.BAD_REQUEST, it.message) }
            .onErrorMap({ it is IllegalArgumentException }) { ResponseStatusException(HttpStatus.BAD_REQUEST, it.message) }
            .onErrorMap({ it is DataIntegrityViolationException }) { ResponseStatusException(HttpStatus.CONFLICT, it.message) }
}