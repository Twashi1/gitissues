package gitissues.issuetag

import jakarta.persistence.Column
import java.io.Serializable
import java.util.Objects

class IssueTagId(
    @Column(name = "project_id")
    var projectId: Long = 0L,
    @Column(name = "issue_id")
    var issueId: Long = 0L,
    @Column(name = "tag_id")
    var tagId: Long = 0L,
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IssueTagId) return false
        return projectId == other.projectId && issueId == other.issueId && tagId == other.tagId
    }

    override fun hashCode(): Int = Objects.hash(projectId, issueId, tagId)
}
