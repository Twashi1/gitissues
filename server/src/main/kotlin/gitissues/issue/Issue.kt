package gitissues.issue

import gitissues.dto.issue.IssueResponse
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "issues")
class Issue(
    @Id
    @Column(name = "uuid7", nullable = false, length = 36)
    var uuid7: String,
    @Column(nullable = false, length = 128)
    var title: String,
    @Column(nullable = false, length = 5000)
    var description: String,
    @Column(nullable = false, length = 50)
    var status: String,
    @Column(name = "list_id")
    var listId: Long? = null,
    @Column(name = "project_id")
    var projectId: Long = 0L,
    @Column(name = "entity")
    var entity: Int? = null,
) {
    fun toResponse(): IssueResponse =
        IssueResponse(
            uuid7 = uuid7,
            title = title,
            description = description,
            status = status,
            listId = listId,
        )
}