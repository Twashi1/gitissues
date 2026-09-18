package gitissues.listener

import gitissues.event.IssueDeletedEvent
import gitissues.native.NativeIssueStore
import gitissues.sync.SyncService
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener

@Component
class IssueDeletedListener(
    private val nativeStore: NativeIssueStore,
    private val syncService: SyncService,
) {
    @Async
    @TransactionalEventListener // AFTER_COMMIT by default
    fun handleIssueDeleted(event: IssueDeletedEvent) {
        // You can decide whether to forward the whole entity (if you kept it) or just the id.
        nativeStore.forwardDeleted(event.getIssueId, event.getIssue)
        // Mark that a change occurred so periodic sync can pick it up
        syncService.markChanges()
    }
}
