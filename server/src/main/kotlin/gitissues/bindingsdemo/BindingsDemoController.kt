package gitissues.bindingsdemo

import gitissues.GitIssues
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for testing the C library through Kotlin bindings.
 * This demonstrates how to use the Kotlin wrapper around the JNI bindings.
 *
 * The actual implementation of tests on the C library should be completed by the user.
 */
@RestController
@RequestMapping("/api/bindings-demo")
class BindingsDemoController {
    /**
     * Initialize the native library.
     * This calls GitIssues.init() which loads the native library and calls init().
     */
    @PostMapping("/init")
    fun initialize(): ResponseEntity<String> {
        GitIssues.init()
        return ResponseEntity.ok("Native library initialized")
    }

    /**
     * Terminate the native library.
     * This calls GitIssues.terminate() which calls terminate() in the native library.
     */
    @PostMapping("/terminate")
    fun terminate(): ResponseEntity<String> {
        GitIssues.terminate()
        return ResponseEntity.ok("Native library terminated")
    }

    /**
     * Load a schema from a file.
     * In a real application, you would load your schema file here.
     * For demonstration, we'll show how the API works.
     */
    @PostMapping("/load-schema")
    fun loadSchema(): ResponseEntity<String> {
        // TODO: Implement actual schema loading
        // Example usage:
        // val schema = GitIssues.loadSchema("path/to/your/schema.iff")
        // return ResponseEntity.ok("Schema loaded with handle: ${schema.handle}")
        return ResponseEntity.ok("Schema loading endpoint - implement actual schema loading")
    }

    /**
     * Create a new issue using the native library.
     * Demonstrates creating an issue through the Kotlin bindings.
     */
    @PostMapping("/create-issue")
    fun createIssue(): ResponseEntity<String> {
        // TODO: Implement actual issue creation
        // Example usage:
        // val schema = GitIssues.loadSchema("path/to/your/schema.iff")
        // val issue = schema.createIssue()
        // return ResponseEntity.ok("Issue created with handle: ${issue.handle}")
        return ResponseEntity.ok("Issue creation endpoint - implement actual issue creation")
    }

    /**
     * Attach a tag to an issue.
     * Demonstrates attaching data to an issue through the native library.
     */
    @PostMapping("/attach-tag")
    fun attachTag(): ResponseEntity<String> {
        // TODO: Implement actual tag attachment
        // Example usage:
        // val schema = GitIssues.loadSchema("path/to/your/schema.iff")
        // val issue = schema.createIssue()
        // issue.attachTag("status", "open".toByteArray())
        // return ResponseEntity.ok("Tag attached")
        return ResponseEntity.ok("Tag attachment endpoint - implement actual tag attachment")
    }

    /**
     * Get a tag from an issue.
     * Demonstrates retrieving data from an issue through the native library.
     */
    @GetMapping("/get-tag/{tagName}")
    fun getTag(
        @PathVariable tagName: String,
    ): ResponseEntity<String> {
        // TODO: Implement actual tag retrieval
        // Example usage:
        // val schema = GitIssues.loadSchema("path/to/your/schema.iff")
        // val issue = schema.createIssue()
        // val data = issue.getTag<ByteArray>(tagName)
        // return ResponseEntity.ok("Tag data: ${data?.contentToString()}")
        return ResponseEntity.ok("Tag retrieval endpoint - implement actual tag retrieval for: $tagName")
    }

    /**
     * Check if an issue has a specific tag.
     * Demonstrates checking for tags through the native library.
     */
    @GetMapping("/has-tag/{tagName}")
    fun hasTag(
        @PathVariable tagName: String,
    ): ResponseEntity<String> {
        // TODO: Implement actual tag checking
        // Example usage:
        // val schema = GitIssues.loadSchema("path/to/your/schema.iff")
        // val issue = schema.createIssue()
        // val hasTag = issue.hasTag(tagName)
        // return ResponseEntity.ok("Has tag $tagName: $hasTag")
        return ResponseEntity.ok("Tag check endpoint - implement actual tag check for: $tagName")
    }
}
