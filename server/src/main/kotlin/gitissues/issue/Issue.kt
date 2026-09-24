package gitissues.issue

import gitissues.dto.issue.IssueResponse
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.Table

@Entity
@Table(name = "issues")
@IdClass(IssueId::class)
class Issue(
    @Id
    @Column(name = "project_id", nullable = false)
    var projectId: Long = 0L,
    @Id
    @Column(name = "id", nullable = false)
    var id: Long = 0L,
    @Column(nullable = false, length = 128)
    var title: String,
    @Column(nullable = false, length = 5000)
    var description: String,
    @Column(nullable = false, length = 50)
    var status: String,
    @Column(name = "list_id")
    var listId: Long? = null,
) {
    fun toResponse(): IssueResponse =
        IssueResponse(
            id = id,
            title = title,
            description = description,
            status = status,
            listId = listId,
        )
}
