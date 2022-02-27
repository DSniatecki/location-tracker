package com.dsniatecki.locationtracker.storage.`object`

import org.reactivestreams.Publisher
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE as JSON
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono

@RestController
@RequestMapping(path = ["/api"])
class ObjectController(
    private val objectService: ObjectService,
) {

    @GetMapping(value = ["/objects/{objectId}"], produces = [JSON])
    fun getObject(@PathVariable(name = "objectId") objectId: String): Publisher<ObjectInstance> =
        objectService.get(objectId)
            .switchIfEmpty(Mono.error(NoSuchElementException("Object with id: '$objectId' does not exist.")))
            .handleErrors()

    @GetMapping(value = ["/objects"], produces = [JSON])
    fun getObjects(@RequestParam(name = "ids") ids: Set<String>): Publisher<ObjectInstance> =
        objectService.getMultiple(ids)

    @PostMapping(value = ["/objects"], consumes = [JSON], produces = [JSON])
    @ResponseStatus(HttpStatus.CREATED)
    fun updateObject(@RequestBody newObject: NewObject): Publisher<ObjectInstance> =
        Mono.just(newObject)
            .map { it.validate() }
            .flatMap { objectService.save(newObject) }
            .handleErrors()

    @PutMapping(value = ["/objects/{objectId}"], consumes = [JSON], produces = [JSON])
    fun updateObject(
        @PathVariable(name = "objectId") objectId: String,
        @RequestBody objectUpdate: ObjectUpdate
    ): Publisher<ObjectInstance> =
        Mono.just(objectUpdate)
            .map { it.validate() }
            .flatMap { objectService.update(objectId, objectUpdate) }
            .switchIfEmpty(Mono.error(NoSuchElementException("Object with id: '$objectId' does not exist.")))
            .handleErrors()

    @DeleteMapping(value = ["/objects/{objectId}"], produces = [JSON])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteObject(@PathVariable(name = "objectId") objectId: String): Publisher<Unit> =
        objectService.delete(objectId)
            .switchIfEmpty(Mono.error(NoSuchElementException("Object with id: '$objectId' does not exist.")))
            .handleErrors()

    private fun <T> Mono<T>.handleErrors(): Mono<T> =
        this.onErrorMap({ it is NoSuchElementException }) { ResponseStatusException(HttpStatus.NOT_FOUND, it.message) }
            .onErrorMap({ it is IllegalStateException }) { ResponseStatusException(HttpStatus.BAD_REQUEST, it.message) }
            .onErrorMap({ it is IllegalArgumentException }) { ResponseStatusException(HttpStatus.BAD_REQUEST, it.message) }
            .onErrorMap({ it is DataIntegrityViolationException }) { ResponseStatusException(HttpStatus.CONFLICT, it.message) }
}