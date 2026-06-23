package gitissues.demo.issue

import gitissues.demo.issue.IssueTag
import gitissues.demo.issue.IssueTagId
import org.springframework.data.jpa.repository.JpaRepository

interface IssueTagRepository : JpaRepository<IssueTag, IssueTagId> {
    fun findAllByIssueId(issueId: Long): List<IssueTag>

    fun findAllByTagId(tagId: Long): List<IssueTag>

    fun findByIssueIdAndTagId(
        issueId: Long,
        tagId: Long,
    ): IssueTag?
}
