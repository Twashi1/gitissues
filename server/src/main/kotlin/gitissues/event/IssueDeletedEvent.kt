package gitissues.event

import gitissues.issue.Issue
import org.springframework.context.ApplicationEvent

/**
 * Fired after an Issue is removed from SQLite.
 */
class IssueDeletedEvent(
    private val issueId: Long,       // we only need the identifier
    private val issue: Issue? = null // optional: keep a copy for logging / audit
) : ApplicationEvent(issueId) {

    val getIssueId: Long
        get() = issueId

    val getIssue: Issue?
        get() = issue
}