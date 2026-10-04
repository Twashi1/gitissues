package gitissues.event

import gitissues.issue.Issue
import org.springframework.context.ApplicationEvent

/**
 * Fired after an Issue is removed from SQLite.
 */
class IssueDeletedEvent(
    private val uuid7: String, // the issue's UUID7 identifier
    private val issue: Issue?, // optional: keep a copy for logging / audit
) : ApplicationEvent(uuid7) {
    val getUuid7: String
        get() = uuid7

    val getIssue: Issue?
        get() = issue
}
