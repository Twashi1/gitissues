package gitissues.event

import gitissues.issue.Issue
import org.springframework.context.ApplicationEvent

/**
 * Fired after an Issue is persisted (CREATE or PATCH) in SQLite.
 */
class IssueSavedEvent(
    private val issue: Issue, // the entity that was saved
) : ApplicationEvent(issue) {
    val getIssue: Issue
        get() = issue
}
