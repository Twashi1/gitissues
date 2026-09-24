package gitissues.issuetag

import gitissues.issuetag.IssueTag
import gitissues.issuetag.IssueTagId
import org.springframework.data.jpa.repository.JpaRepository

interface IssueTagRepository : JpaRepository<IssueTag, IssueTagId> {
    fun findAllByProjectIdAndIssueId(
        projectId: Long,
        issueId: Long,
    ): List<IssueTag>

    fun findAllByProjectIdAndTagId(
        projectId: Long,
        tagId: Long,
    ): List<IssueTag>

    fun findByProjectIdAndIssueIdAndTagId(
        projectId: Long,
        issueId: Long,
        tagId: Long,
    ): IssueTag?
}
