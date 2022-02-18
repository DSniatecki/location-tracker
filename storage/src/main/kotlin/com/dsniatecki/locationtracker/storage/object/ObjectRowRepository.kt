package com.dsniatecki.locationtracker.storage.`object`

import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface ObjectRowRepository : ReactiveCrudRepository<ObjectRow, String>