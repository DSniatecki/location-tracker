package com.dsniatecki.locationtracker.storage.`object`

import com.dsniatecki.locationtracker.storage.model.internal.NewObjectDto as NewObjectInternalDto
import com.dsniatecki.locationtracker.storage.model.internal.ObjectInstanceDto as ObjectInstanceInternalDto
import com.dsniatecki.locationtracker.storage.model.internal.ObjectUpdateDto as ObjectUpdateInternalDto
import com.dsniatecki.locationtracker.storage.model.pub.NewObjectDto as NewObjectPublicDto
import com.dsniatecki.locationtracker.storage.model.pub.ObjectInstanceDto as ObjectInstancePublicDto
import com.dsniatecki.locationtracker.storage.model.pub.ObjectUpdateDto as ObjectUpdatePublicDto

fun ObjectInstance.toInternalDto(): ObjectInstanceInternalDto =
    ObjectInstanceInternalDto()
        .id(this.id)
        .name(this.name)
        .imageUrl(this.imageUrl)
        .createdAt(this.createdAt)
        .updatedAt(this.updatedAt)

fun ObjectInstance.toPublicDto(): ObjectInstancePublicDto =
    ObjectInstancePublicDto()
        .id(this.id)
        .name(this.name)
        .imageUrl(this.imageUrl)
        .createdAt(this.createdAt)
        .updatedAt(this.updatedAt)

fun NewObjectInternalDto.toNewObject(): NewObject =
    NewObject(id = this.id, name = this.name, imageUrl = this.imageUrl)

fun NewObjectPublicDto.toNewObject(): NewObject =
    NewObject(id = this.id, name = this.name, imageUrl = this.imageUrl)

fun ObjectUpdateInternalDto.toObjectUpdate(): ObjectUpdate = ObjectUpdate(this.name, this.imageUrl)

fun ObjectUpdatePublicDto.toObjectUpdate(): ObjectUpdate = ObjectUpdate(this.name, this.imageUrl)

