package com.dsniatecki.locationtracker.storage.`object`

import com.dsniatecki.locationtracker.storage.api.pub.ObjectsApi
import com.dsniatecki.locationtracker.storage.model.pub.NewObjectDto
import com.dsniatecki.locationtracker.storage.model.pub.ObjectInstanceDto
import com.dsniatecki.locationtracker.storage.model.pub.ObjectUpdateDto
import java.util.Optional
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping(path = ["/api/public"])
class ObjectPublicController(private val objectService: ObjectService) : ObjectsApi {

    override fun getObject(objectId: String, exchange: ServerWebExchange): Mono<ResponseEntity<ObjectInstanceDto>> =
        objectService.get(objectId)
            .map { ResponseEntity.ok(it.toPublicDto()) }
            .switchIfEmpty(Mono.error(NoSuchElementException("Object with id: '$objectId' does not exist.")))

    override fun getObjects(
        objectIds: Optional<Set<String>>,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<Flux<ObjectInstanceDto>>> =
        Mono.just(objectIds.map { objectService.getMultiple(it) }.orElseGet { objectService.getAll() })
            .map { flux -> flux.map { it.toPublicDto() } }
            .map { ResponseEntity.ok(it) }

    override fun createObject(
        newObjectDto: Mono<NewObjectDto>,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<ObjectInstanceDto>> =
        newObjectDto.flatMap { objectService.save(it.toNewObject()) }
            .map { ResponseEntity.status(HttpStatus.CREATED).body(it.toPublicDto()) }

    override fun updateObject(
        objectId: String,
        objectUpdateDto: Mono<ObjectUpdateDto>,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<ObjectInstanceDto>> =
        objectUpdateDto.flatMap { objectService.update(objectId, it.toObjectUpdate()) }
            .map { ResponseEntity.ok(it.toPublicDto()) }
            .switchIfEmpty(Mono.error(NoSuchElementException("Object with id: '$objectId' does not exist.")))

    override fun deleteObject(objectId: String, exchange: ServerWebExchange): Mono<ResponseEntity<Void>> =
        objectService.delete(objectId)
            .map { ResponseEntity.noContent().build<Void>() }
            .switchIfEmpty(Mono.error(NoSuchElementException("Object with id: '$objectId' does not exist.")))
}