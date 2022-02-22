package com.dsniatecki.locationtracker.storage.`object`

import com.dsniatecki.locationtracker.commons.utils.TimeSupplier
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class ObjectService(
    private val objectRepository: ObjectRepository,
    private val timeSupplier: TimeSupplier,
) {

    fun get(objectId: String): Mono<ObjectInstance> = objectRepository.findById(objectId)

    fun getAll(): Flux<ObjectInstance> = objectRepository.findAll().sort(Comparator.comparing { it.createdAt })

    fun save(objectData: ObjectData): Mono<ObjectInstance> = objectRepository.save(createNewObject(objectData))

    fun update(objectId: String, objectData: ObjectData): Mono<ObjectInstance> =
        objectRepository.findById(objectId)
            .flatMap { objectRepository.save(updateObject(it, objectData)) }

    fun delete(objectId: String): Mono<Unit> =
        objectRepository.findById(objectId)
            .flatMap { objectRepository.delete(objectId, timeSupplier.now()).switchIfEmpty(Mono.just(Unit)) }

    private fun createNewObject(objectData: ObjectData): ObjectInstance =
        ObjectInstance(
            id = generateId(),
            name = objectData.name,
            imageUrl = objectData.imageUrl,
            createdAt = timeSupplier.now().atOffset(timeSupplier.zoneOffset()),
            updatedAt = null
        )

    private fun updateObject(objectInstance: ObjectInstance, objectData: ObjectData): ObjectInstance =
        objectInstance.copy(
            name = objectData.name,
            imageUrl = objectData.imageUrl,
            updatedAt = timeSupplier.now().atOffset(timeSupplier.zoneOffset())
        )
}