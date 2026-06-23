package gitissues

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(Exception::class)
    fun handleGeneric(e: Exception): Map<String, String> {
        log.error("Unhandled backend error", e)

        return mapOf(
            "error" to "Internal Server Error",
        )
    }

    @ExceptionHandler(ResponseStatusException::class)
    fun handleStatus(e: ResponseStatusException): Map<String, String> {
        log.warn("Request error: {}", e.reason)

        return mapOf(
            "error" to (e.reason ?: "Request error"),
        )
    }
}
