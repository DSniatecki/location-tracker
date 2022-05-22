package com.dsniatecki.locationtracker.archiver.config

import javax.validation.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ControllerExceptionHandler {

    @ExceptionHandler(IllegalStateException::class, IllegalArgumentException::class, ConstraintViolationException::class)
    fun handleBadRequests(exc: Exception): ResponseEntity<String> = ResponseEntity.badRequest().build()

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(exc: NoSuchElementException): ResponseEntity<Void> =
        ResponseEntity.notFound().build()

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolationException(exc: DataIntegrityViolationException): ResponseEntity<String> =
        ResponseEntity.status(HttpStatus.CONFLICT).build()
}