package gitissues.native

import gitissues.jni.GitIssues
import org.springframework.stereotype.Component

/**
 * Kotlin wrapper around the JNI GitIssues bindings.
 * Provides a more idiomatic Kotlin API with automatic resource management.
 */
@Component
object GitIssues {
    /**
     * Initialize the native library.
     * This loads the native library and calls the init() function.
     */
    fun init() {
        gitissues.jni.GitIssues.init()
    }

    /**
     * Terminate the native library.
     * This calls the terminate() function in the native library.
     */
    fun terminate() {
        gitissues.jni.GitIssues.terminate()
    }

    /**
     * Load a schema from a file.
     * Returns a Schema object that manages the schema's lifetime.
     *
     * @param filename Path to the schema file
     * @return Schema object that manages the native schema handle
     */
    fun loadSchema(filename: String): Schema {
        val handle = gitissues.jni.GitIssues.loadSchema(filename)
        return Schema(handle)
    }
}