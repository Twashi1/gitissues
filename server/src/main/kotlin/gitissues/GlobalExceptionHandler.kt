package gitissues

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(ResponseStatusException::class)
    fun handleStatus(e: ResponseStatusException): ResponseEntity<Map<String, String>> {
        log.warn("Request error: {}", e.reason)

        return ResponseEntity
            .status(e.statusCode)
            .body(mapOf("error" to (e.reason ?: "Request error")))
    }

    @ExceptionHandler(IllegalArgumentException::class, NoSuchElementException::class)
    fun handleNotFound(e: RuntimeException): ResponseEntity<Map<String, String>> {
        log.warn("Resource not found: {}", e.message)

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(mapOf("error" to (e.message ?: "Not found")))
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneric(e: Exception): ResponseEntity<Map<String, String>> {
        log.error("Unhandled backend error", e)

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(mapOf("error" to "Internal Server Error"))
    }
}
