package gitissues.issue

import gitissues.dto.issue.IssueResponse
import gitissues.native.GitIssues
import gitissues.native.Schema
import gitissues.native.NativeIssue
import gitissues.native.UUID7
import gitissues.project.Project
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import java.io.File
import java.nio.ByteBuffer

@Service
class IssueImportService(
    private val entityManager: EntityManager,
    private val issueRepo: IssueRepository
) {

    @Transactional
    fun loadIFF(projectId: Long, path: String): List<IssueResponse> {
        // 1. Grab the export directory from the project table
        val project: Project = entityManager.find(Project::class.java, projectId) ?: return emptyList()

        val exportDirectory = project.exportDirectory

        // 2. Look for schema.json at the export directory
        val schemaFile = File(exportDirectory, "schema.json")
        if (!schemaFile.exists()) {
            return emptyList()
        }

        // 3. Load using the native library
        val schema = GitIssues.loadSchema(schemaFile.absolutePath)

        // 4. Validate path exists beforehand
        val iffFile = File(path)
        if (!iffFile.exists()) {
            schema.close()
            return emptyList()
        }

        // 5. Load IFF issues using the schema
        val nativeIssues: List<NativeIssue> = schema.loadIFF(path)

        // 6. Convert native Issue objects to IssueResponse, adding each to the database
        val issueResponses = mutableListOf<IssueResponse>()

        for (nativeIssue in nativeIssues) {
            // Get tags from the native issue
            val uuid7Bytes = nativeIssue.getTag("id") ?: byteArrayOf()
            val uuid7 = UUID7.toHexString(uuid7Bytes)
            val title = nativeIssue.getTag("title")?.toString() ?: ""
            val description = nativeIssue.getTag("description")?.toString() ?: ""
            val status = nativeIssue.getTag("status")?.toString() ?: ""
            val listIdBytes = nativeIssue.getTag("listid")
            val listId = listIdBytes?.let { ByteBuffer.wrap(it).getLong() } ?: null

            // Check if issue already exists in database by uuid7
            val existingIssue = issueRepo.findByUuid7(uuid7)

            if (existingIssue == null) {
                // Create new issue
                val issue = Issue(
                    uuid7 = uuid7,
                    title = title,
                    description = description,
                    status = status,
                    listId = listId,
                    projectId = projectId,
                    entity = null
                )
                entityManager.persist(issue)
                issueResponses.add(issue.toResponse())
            } else {
                // Update existing issue
                existingIssue.title = title
                existingIssue.description = description
                existingIssue.status = status
                existingIssue.listId = listId
                existingIssue.projectId = projectId

                entityManager.persist(existingIssue)
                issueResponses.add(existingIssue.toResponse())
            }
        }

        schema.close()

        return issueResponses
    }
}