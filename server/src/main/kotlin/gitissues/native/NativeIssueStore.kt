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
        //     issue.uuid7,
        //     issue.title,
        //     issue.description,
        //     issue.status,
        //     issue.listId ?: -1L   // or whatever sentinel your native side expects
        // )
        // For now just a placeholder:
        System.out.println("[NativeStore] Saved issue ${issue.uuid7}: ${issue.title}")
    }

    /**
     * Called after an Issue is DELETED from SQLite.
     *
     * @param uuid7 the issue's UUID7 identifier
     * @param issue   the original Issue entity (may be null if you didn't pass it in the event)
     */
    fun forwardDeleted(
        uuid7: String,
        issue: Issue?,
    ) {
        // TODO: Implement your native call, e.g.
        // NativeLibrary.issueDelete(uuid7)
        System.out.println("[NativeStore] Deleted issue $uuid7")
    }
}
