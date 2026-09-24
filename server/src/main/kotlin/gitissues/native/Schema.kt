package gitissues.native

import java.lang.AutoCloseable

/**
 * Wrapper around a native schema handle that manages the schema's lifetime.
 * Implements AutoCloseable for deterministic resource cleanup.
 */
class Schema internal constructor(
    private val _handle: Long,
) : AutoCloseable {
    /**
     * The native handle for this schema.
     * @return the native handle
     */
    val handle: Long
        get() = _handle

    /**
     * Creates a new issue using this schema.
     * Returns an Issue object that manages the issue's lifetime.
     *
     * @return Issue object representing the newly created issue
     */
    fun createIssue(): Issue {
        val issueHandle = gitissues.jni.GitIssues.createIssue(handle)
        return Issue(issueHandle, this) // Pass reference to schema to keep it alive
    }

    /**
     * Loads an IFF file full of issues, given this schema.
     * Returns a list of Issue objects.
     *
     * @param filename Path to the IFF file
     * @return List of Issue objects
     */
    fun loadIFF(filename: String): List<Issue> {
        val issueHandles = gitissues.jni.GitIssues.loadIFF(handle, filename)
        return issueHandles.map { Issue(it, this) }
    }

    /**
     * Saves a list of issues to an IFF file, given this schema.
     *
     * @param filename Path to the IFF file
     * @param issues List of Issue objects to save
     */
    fun saveIFF(
        filename: String,
        issues: List<Issue>,
    ) {
        val issueHandles = issues.map { it.handle }
        gitissues.jni.GitIssues.saveIFF(handle, filename, issueHandles)
    }

    /**
     * Frees the native schema handle.
     * Called automatically when used in try-with-resources or apply { use { } },
     * but can also be called explicitly to free resources early.
     */
    override fun close() {
        gitissues.jni.GitIssues.freeSchema(handle)
    }
}
