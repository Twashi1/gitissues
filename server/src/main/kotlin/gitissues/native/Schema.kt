package gitissues.native

import gitissues.jni.GitIssues
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
     * Returns a NativeIssue object that manages the issue's lifetime.
     *
     * @return NativeIssue representing the newly created issue
     */
    fun createIssue(): NativeIssue {
        val issueHandle = GitIssues.createIssue(handle)
        return NativeIssue(issueHandle, this) // Pass reference to schema to keep it alive
    }

    fun isNullIssue(issue: NativeIssue): Boolean = GitIssues.isNullIssue(handle, issue.handle)

    fun removeIssues(issues: List<NativeIssue>) {
        GitIssues.removeIssues(handle, issues.map { it.handle })
    }

    /**
     * Loads an IFF file full of issues, given this schema.
     * Returns a list of NativeIssue objects.
     *
     * @param filename Path to the IFF file
     * @return List of NativeIssue objects
     */
    fun loadIFF(filename: String): List<NativeIssue> {
        val issueHandles = GitIssues.loadIFF(handle, filename)
        return issueHandles.map { NativeIssue(it, this) }
    }

    /**
     * Saves a list of NativeIssue objects to an IFF file, given this schema.
     *
     * @param filename Path to the IFF file
     * @param issues List of NativeIssue objects to save
     */
    fun saveIFF(
        filename: String,
        issues: List<NativeIssue>,
    ) {
        val issueHandles = issues.map { it.handle }
        GitIssues.saveIFF(handle, filename, issueHandles)
    }

    /**
     * Frees the native schema handle.
     * Called automatically when used in try-with-resources or apply { use { } },
     * but can also be called explicitly to free resources early.
     */
    override fun close() {
        GitIssues.freeSchema(handle)
    }

    /**
     * Get or create a UUID7 for an issue within this schema.
     * If the issue already has a UUID7 component registered, it returns the existing one.
     * Otherwise, it generates a new UUID7 and registers it with the issue.
     *
     * @param issue The issue to get or create a UUID7 for
     * @return A 16-byte array representing the UUID7
     */
    fun getOrCreateUUID(issue: NativeIssue): ByteArray {
        val bytes = GitIssues.getOrCreateUUID(handle, issue.handle)
        // bytes is already a ByteArray from JNI, return a copy
        return bytes
    }

    /**
     * Companion object with utility methods for the Schema.
     */
    companion object
}