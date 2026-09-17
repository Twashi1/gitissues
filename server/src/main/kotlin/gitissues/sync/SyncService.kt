package gitissues.sync

import org.slf4j.LoggerFactory
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicBoolean

@Component
@EnableScheduling
class SyncService {
    private val log = LoggerFactory.getLogger(SyncService::class.java)

    // Flag indicating that changes have occurred since last sync
    private val hasChanges = AtomicBoolean(false)

    /** Called by event listeners when a change occurs */
    fun markChanges() {
        hasChanges.set(true)
        log.debug("Changes marked for sync")
    }

    /**
     * Called periodically (every 3 seconds) to sync if changes have occurred.
     * Resets the flag after sync.
     */
    @Scheduled(fixedDelay = 3000)
    fun maybeSync() {
        if (hasChanges.getAndSet(false)) {
            log.info("Running periodic sync due to changes")
            performSync()
        }
    }

    /** Called on application startup if there are pending changes */
    @EventListener
    fun onStartup(event: ContextRefreshedEvent) {
        if (hasChanges.getAndSet(false)) {
            log.info("Running sync on startup due to pending changes")
            performSync()
        }
    }

    /** Called on application exit if there are pending changes */
    @EventListener
    fun onClose(event: ContextClosedEvent) {
        syncOnExit()
    }

    /** Internal method to perform sync on exit (called by onClose) */
    private fun syncOnExit() {
        if (hasChanges.getAndSet(false)) {
            log.info("Running sync on exit due to pending changes")
            performSync()
        }
    }

    /**
     * Perform the actual sync from native library store to database.
     * Replace the TODO with your implementation.
     */
    private fun performSync() {
        // TODO: Implement sync logic here
        // Example:
        // 1. Read all issues from native library store
        // 2. For each issue, upsert into SQLite database
        // 3. Optionally, delete issues that no longer exist in native store
        log.warn("Sync not yet implemented - replace performSync() with your native-to-DB sync logic")
    }
}
