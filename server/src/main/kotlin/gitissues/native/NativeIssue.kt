package gitissues.native

/**
 * Wrapper around a native issue handle that manages the issue's lifetime.
 * Issues are tied to a schema and should not outlive their schema.
 */
class NativeIssue internal constructor(
    private val _handle: Long,
    private val schema: Schema,
) {
    val handle: Long
        get() = _handle

    fun attachTag(
        tagName: String,
        data: ByteArray,
    ) {
        gitissues.jni.GitIssues.attachTag(schema.handle, handle, tagName, data)
    }

    fun attachTagByID(
        tagID: Long,
        data: ByteArray,
    ) {
        gitissues.jni.GitIssues.attachTagByID(schema.handle, handle, tagID, data)
    }

    fun detachTag(tagName: String) {
        gitissues.jni.GitIssues.detachTag(schema.handle, handle, tagName)
    }

    fun detachTagByID(tagID: Long) {
        gitissues.jni.GitIssues.detachTagByID(schema.handle, handle, tagID)
    }

    fun getTag(tagName: String): ByteArray? = gitissues.jni.GitIssues.getTag(schema.handle, handle, tagName)

    fun getTagByID(tagID: Long): ByteArray? = gitissues.jni.GitIssues.getTagByID(schema.handle, handle, tagID)

    fun hasTag(tagName: String): Boolean = gitissues.jni.GitIssues.hasTag(schema.handle, handle, tagName)

    fun hasTagByID(tagID: Long): Boolean = gitissues.jni.GitIssues.hasTagByID(schema.handle, handle, tagID)
}
