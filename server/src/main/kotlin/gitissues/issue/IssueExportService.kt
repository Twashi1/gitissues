package gitissues.issue

import gitissues.dto.issue.IssueResponse
import gitissues.native.GitIssues
import gitissues.native.Schema
import gitissues.native.NativeIssue
import gitissues.project.Project
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import java.io.File

@Service
class IssueExportService(
    private val entityManager: EntityManager,
    private val issueRepo: IssueRepository
) {

    @Transactional
    fun exportIFF(projectId: Long, path: String): Boolean {
        // 1. Grab the project to verify it exists
        val project: Project = entityManager.find(Project::class.java, projectId) ?: return false

        // 2. Look for schema.json at the project's export directory
        val schemaFile = File(project.exportDirectory, "schema.json")
        var schema: Schema

        if (schemaFile.exists()) {
            // 3. Load existing schema from export directory
            schema = GitIssues.loadSchema(schemaFile.absolutePath)
        } else {
            // 4. Create a new schema using the path
            schema = GitIssues.loadSchema(path)
        }

        // 5. Get all issues for this project
        val issues = issueRepo.findAll().filter { it.projectId == projectId }

        // 6. Build native issues from database issues
        val nativeIssues = mutableListOf<NativeIssue>()

        for (issue in issues) {
            // Create a new native issue under this schema
            // Use the Schema's companion object to access GitIssues.createIssue
            val issueHandle = gitissues.jni.GitIssues.createIssue(schema.handle)
            val nativeIssue = NativeIssue(issueHandle, schema)

            // 7. Attach tags from the database issue using attachTag
            // Map issue properties to native tags
            // "listid" -> list_id
            // "description" -> description
            // "status" -> status
            // "title" -> title
            // "id" -> uuid7

            nativeIssue.attachTag("title", issue.title.toByteArray())
            nativeIssue.attachTag("description", issue.description.toByteArray())
            nativeIssue.attachTag("status", issue.status.toByteArray())

            // listid - only if not null
            if (issue.listId != null) {
                nativeIssue.attachTag("listid", issue.listId.toString().toByteArray())
            }

            // id (uuid7) - only if not null/empty
            if (issue.uuid7.isNotEmpty()) {
                nativeIssue.attachTag("id", issue.uuid7.toByteArray())
            }

            nativeIssues.add(nativeIssue)
        }

        // 8. Save all native issues to IFF file using saveIFF
        // Schema.saveIFF takes List<NativeIssue> directly
        schema.saveIFF(path, nativeIssues)

        // 9. Free the schema resources
        schema.close()

        return true
    }
}