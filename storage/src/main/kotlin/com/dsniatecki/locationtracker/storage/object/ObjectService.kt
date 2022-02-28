package com.dsniatecki.locationtracker.storage.`object`

import com.dsniatecki.locationtracker.commons.utils.TimeSupplier
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class ObjectService(
    private val objectRepository: ObjectRepository,
    private val timeSupplier: TimeSupplier,
) {

    fun get(objectId: String): Mono<ObjectInstance> = objectRepository.findById(objectId)

    fun getMultiple(objectIds: Set<String>): Flux<ObjectInstance> =
        objectRepository.findByIds(objectIds).sort(Comparator.comparing { it.createdAt })

    fun save(newObject: NewObject): Mono<ObjectInstance> = objectRepository.save(createNewObject(newObject))

    fun update(objectId: String, objectData: ObjectUpdate): Mono<ObjectInstance> =
        objectRepository.findById(objectId)
            .flatMap { objectRepository.save(updateObject(it, objectData)) }

    fun delete(objectId: String): Mono<Unit> =
        objectRepository.findById(objectId)
            .flatMap { objectRepository.delete(objectId, timeSupplier.now()).switchIfEmpty(Mono.just(Unit)) }

    private fun createNewObject(newObject: NewObject): ObjectInstance =
        ObjectInstance(
            id = newObject.id ?: generateId(),
            name = newObject.name,
            imageUrl = newObject.imageUrl,
            createdAt = timeSupplier.now().atOffset(timeSupplier.zoneOffset()),
            updatedAt = null
        )

    private fun updateObject(objectInstance: ObjectInstance, objectData: ObjectUpdate): ObjectInstance =
        objectInstance.copy(
            name = objectData.name,
            imageUrl = objectData.imageUrl,
            updatedAt = timeSupplier.now().atOffset(timeSupplier.zoneOffset())
        )
}