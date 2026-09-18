package gitissues.listener

import gitissues.event.IssueSavedEvent
import gitissues.native.NativeIssueStore
import gitissues.sync.SyncService
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener

@Component
class IssueSavedListener(
    private val nativeStore: NativeIssueStore,
    private val syncService: SyncService,
) {
    @Async
    @TransactionalEventListener // defaults to AFTER_COMMIT
    fun handleIssueSaved(event: IssueSavedEvent) {
        // This runs on a Spring‑managed thread‑pool thread.
        nativeStore.forwardSaved(event.getIssue)
        // Mark that a change occurred so periodic sync can pick it up
        syncService.markChanges()
    }
}
