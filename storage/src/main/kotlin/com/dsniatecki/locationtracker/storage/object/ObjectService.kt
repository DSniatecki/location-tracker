package com.dsniatecki.locationtracker.storage.`object`

import com.dsniatecki.locationtracker.storage.utils.TimeSupplier
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class ObjectService(
    private val objectRepository: ObjectRepository,
    private val timeSupplier: TimeSupplier,
) {

    fun get(objectId: String): Mono<ExistingObject> = objectRepository.findById(objectId)

    fun save(newObject: NewObject): Mono<ExistingObject> = objectRepository.save(createNewObject(newObject))

    fun getAll(): Flux<ExistingObject> = objectRepository.findAll().sort(Comparator.comparing { it.createdAt })

    private fun createNewObject(newObject: NewObject): ExistingObject =
        ExistingObject(
            id = generateId(),
            name = newObject.name,
            imageUrl = newObject.name,
            createdAt = timeSupplier.now().atOffset(timeSupplier.zoneOffset())
        )
}