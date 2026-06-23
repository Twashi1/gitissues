package gitissues.issuetag

import gitissues.issuetag.IssueTag
import gitissues.issuetag.IssueTagId
import org.springframework.data.jpa.repository.JpaRepository

interface IssueTagRepository : JpaRepository<IssueTag, IssueTagId> {
    fun findAllByIssueId(issueId: Long): List<IssueTag>

    fun findAllByTagId(tagId: Long): List<IssueTag>

    fun findByIssueIdAndTagId(
        issueId: Long,
        tagId: Long,
    ): IssueTag?
}
