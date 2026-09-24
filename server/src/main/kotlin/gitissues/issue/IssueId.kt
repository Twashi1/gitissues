package gitissues.issue

import jakarta.persistence.Column
import java.io.Serializable
import java.util.Objects

class IssueId(
    @Column(name = "project_id")
    var projectId: Long = 0L,
    @Column(name = "id")
    var id: Long = 0L,
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IssueId) return false
        return projectId == other.projectId && id == other.id
    }

    override fun hashCode(): Int = Objects.hash(projectId, id)
}
