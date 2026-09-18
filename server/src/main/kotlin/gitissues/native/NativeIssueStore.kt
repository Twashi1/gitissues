package gitissues.native

import gitissues.issue.Issue
import org.springframework.stereotype.Component

/**
 * Thin wrapper around your native library.
 * Spring will create a single bean of this type; the @Async listeners call its methods
 * on background threads.
 */
@Component
class NativeIssueStore {
    /**
     * Called after an Issue is INSERTED or UPDATED in SQLite.
     *
     * @param issue the fully populated Issue entity (detached – safe to use across threads)
     */
    fun forwardSaved(issue: Issue) {
        // TODO: Implement your native call, e.g.
        // NativeLibrary.issueSave(
        //     issue.id,
        //     issue.title,
        //     issue.description,
        //     issue.status,
        //     issue.listId ?: -1L   // or whatever sentinel your native side expects
        // )
        // For now just a placeholder:
        System.out.println("[NativeStore] Saved issue ${issue.id}: ${issue.title}")
    }

    /**
     * Called after an Issue is DELETED from SQLite.
     *
     * @param issueId the primary key of the removed row
     * @param issue   the original Issue entity (may be null if you didn't pass it in the event)
     */
    fun forwardDeleted(
        issueId: Long,
        issue: Issue?,
    ) {
        // TODO: Implement your native call, e.g.
        // NativeLibrary.issueDelete(issueId)
        System.out.println("[NativeStore] Deleted issue $issueId")
    }
}
