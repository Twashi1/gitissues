package gitissues.issue

import gitissues.dto.issue.IssueCreateRequest
import gitissues.dto.issue.IssuePatchRequest
import gitissues.dto.issue.IssueResponse
import gitissues.event.IssueDeletedEvent
import gitissues.event.IssueSavedEvent
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class IssueService(
    private val repo: IssueRepository,
    private val eventPublisher: ApplicationEventPublisher,
) {
    private val log = LoggerFactory.getLogger(IssueService::class.java)

    fun all(
        projectId: Long,
        listId: Long? = null,
    ): List<IssueResponse> {
        val issues =
            if (listId != null) {
                repo.findAllByProjectIdAndListIdOrderByDisplayOrderAsc(projectId, listId)
            } else {
                repo.findAllByUuid7OrderByUuid7Desc(projectId)
            }
        return issues.map { it.toResponse() }
    }

    fun allByProjectIdAndListId(
        projectId: Long,
        listId: Long,
    ): List<IssueResponse> =
        repo.findAllByProjectIdAndListIdOrderByDisplayOrderAsc(projectId, listId).map {
            it.toResponse()
        }

    fun allByProjectId(projectId: Long): List<IssueResponse> = repo.findAllByUuid7OrderByUuid7Desc(projectId).map { it.toResponse() }

    fun getByUuid7(
        projectId: Long,
        listId: Long,
        uuid7: String,
    ): IssueResponse {
        val issue =
            repo.findByProjectIdAndListIdAndUuid7(projectId, listId, uuid7)?.firstOrNull() ?: run {
                throw ResponseStatusException(HttpStatus.NOT_FOUND, "Issue $uuid7 not found in list $listId")
            }
        return issue.toResponse()
    }

    fun get(
        projectId: Long,
        listId: Long,
        uuid7: String,
    ): IssueResponse {
        val issue =
            repo.findByProjectIdAndListIdAndUuid7(projectId, listId, uuid7)?.firstOrNull() ?: run {
                throw ResponseStatusException(HttpStatus.NOT_FOUND, "Issue $uuid7 not found")
            }
        return issue.toResponse()
    }

    fun create(
        projectId: Long,
        listId: Long,
        req: IssueCreateRequest,
    ): IssueResponse {
        val uuid7 =
            req.uuid7 ?: java.util.UUID
                .randomUUID()
                .toString()
        val issue =
            Issue(
                uuid7 = uuid7,
                title = req.title,
                description = req.description,
                status = req.status,
                listId = listId,
                projectId = projectId,
                // Set displayOrder to max+1 for this list, or 0 if issue has no list
                displayOrder = (repo.findMaxDisplayOrderByListId(listId, projectId) ?: 0) + 1,
            )
        val saved = repo.save(issue)
        eventPublisher.publishEvent(IssueSavedEvent(saved))
        return saved.toResponse()
    }

    @Transactional
    fun delete(
        projectId: Long,
        listId: Long,
        uuid7: String,
    ) {
        val issue =
            repo.findByProjectIdAndListIdAndUuid7(projectId, listId, uuid7)?.firstOrNull() ?: run {
                throw ResponseStatusException(HttpStatus.NOT_FOUND, "Issue $uuid7 not found in list $listId")
            }

        repo.delete(issue)
        eventPublisher.publishEvent(IssueDeletedEvent(uuid7, issue))
    }

    @Transactional
    fun patch(
        projectId: Long,
        listId: Long,
        uuid7: String,
        req: IssuePatchRequest,
    ): IssueResponse {
        log.info("Patch request: {}", req)

        val issue =
            repo.findByProjectIdAndListIdAndUuid7(projectId, listId, uuid7)?.firstOrNull() ?: run {
                throw ResponseStatusException(HttpStatus.NOT_FOUND, "Issue $uuid7 not found in list $listId")
            }

        val oldListId = issue.listId
        val newListId = req.listId ?: oldListId
        val listIsBeingChanged = req.listId != null && req.listId != oldListId
        issue.title = req.title ?: issue.title
        issue.description = req.description ?: issue.description
        issue.status = req.status ?: issue.status
        issue.listId = newListId
        // If the list is being changed, set displayOrder to max+1 for the new list.
        issue.displayOrder = if (listIsBeingChanged) {
            (repo.findMaxDisplayOrderByListId(projectId, newListId) ?: 0) + 1
        } else {
            issue.displayOrder
        }

        val saved = repo.save(issue)
        eventPublisher.publishEvent(IssueSavedEvent(saved))
        return saved.toResponse()
    }

    /** Reorder displayOrders in a list to be sequential (1, 2, 3, ...).
     *  Useful when displayOrders have become fragmented or duplicated.
     *
     *  If orderAfter is provided (non-null), only issues with displayOrder >= orderAfter
     *  will be re-assigned sequentially starting from orderAfter + 1;
     *  issues with displayOrder < orderAfter keep their current displayOrder.
     *  This creates a gap exactly at orderAfter (no issue has displayOrder == orderAfter
     *  after the change), which serves to create space for a later patch request.
     *  If orderAfter is null, all displayOrders are re-assigned sequentially (current behavior).
     */
    @Transactional
    fun reorderDisplayOrders(projectId: Long, listId: Long, orderAfter: Integer?): Unit {
        val issues = repo.findAllByProjectIdAndListIdOrderByDisplayOrderAsc(projectId, listId)
        if (orderAfter == null) {
            // Current behavior: reassign all displayOrders sequentially
            for ((index, issue) in issues.withIndex()) {
                issue.displayOrder = index + 1
            }
        } else {
            // Only reassign issues with displayOrder >= orderAfter
            // Keep issues with displayOrder < orderAfter as-is
            // Reassign displayOrders sequentially starting from orderAfter + 1
            // so that a gap is created exactly at orderAfter
            val orderAfterInt = orderAfter!!.toInt() - 1
            val issuesToReassign = issues.filter { it.displayOrder > orderAfterInt }
            var currentOrder = orderAfterInt + 2
            for (issue in issuesToReassign) {
                issue.displayOrder = currentOrder
                currentOrder += 1
            }
        }
        repo.saveAll(issues)
    }

    /** Reorder an issue within a list by moving it from sourceIndex to targetIndex.
     *  This reassigns all displayOrders sequentially (1, 2, 3, ...) after the move.
     *
     *  @param projectId The ID of the project
     *  @param listId The ID of the issue list
     *  @param sourceIndex The current index of the issue to move (0-based)
     *  @param targetIndex The target index to move the issue to (0-based)
     */
    @Transactional
    fun reorderIssueWithinList(projectId: Long, listId: Long, sourceIndex: Int, targetIndex: Int): Unit {
        // Validate indices
        val issues = repo.findAllByProjectIdAndListIdOrderByDisplayOrderAsc(projectId, listId)
        if (sourceIndex < 0 || sourceIndex >= issues.size) {
            throw IllegalArgumentException("Source index out of bounds: $sourceIndex")
        }
        if (targetIndex < 0 || targetIndex > issues.size) {
            throw IllegalArgumentException("Target index out of bounds: $targetIndex")
        }

        // Convert to mutable list for easier manipulation
        val mutableIssues = issues.toMutableList()

        // targetIndex is the insertion index after the source item has been removed.
        val issueToMove = mutableIssues.removeAt(sourceIndex)
        val insertionIndex = targetIndex.coerceIn(0, mutableIssues.size)
        mutableIssues.add(insertionIndex, issueToMove)

        // Reassign displayOrders sequentially (1, 2, 3, ...)
        for ((index, issue) in mutableIssues.withIndex()) {
            issue.displayOrder = index + 1
        }

        // Save all issues
        repo.saveAll(mutableIssues)
    }

    /** Move an issue from one list to another by specifying the source and target indices.
     *  This reassigns all displayOrders sequentially (1, 2, 3, ...) in both lists after the move.
     *
     *  @param projectId The ID of the project
     *  @param sourceListId The ID of the source issue list
     *  @param targetListId The ID of the target issue list
     *  @param sourceIndex The current index of the issue to move in the source list (0-based)
     *  @param targetIndex The target index to move the issue to in the target list (0-based)
     */
    @Transactional
    fun moveIssueToList(
        projectId: Long,
        sourceListId: Long,
        targetListId: Long,
        sourceIndex: Int,
        targetIndex: Int
    ): Unit {
        // Validate that source and target lists are different
        if (sourceListId == targetListId) {
            throw IllegalArgumentException("Source and target lists must be different")
        }

        // Load issues from both lists
        val sourceIssues = repo.findAllByProjectIdAndListIdOrderByDisplayOrderAsc(projectId, sourceListId)
        val targetIssues = repo.findAllByProjectIdAndListIdOrderByDisplayOrderAsc(projectId, targetListId)

        // Validate indices
        if (sourceIndex < 0 || sourceIndex >= sourceIssues.size) {
            throw IllegalArgumentException("Source index out of bounds: $sourceIndex")
        }
        if (targetIndex < 0 || targetIndex > targetIssues.size) {
            throw IllegalArgumentException("Target index out of bounds: $targetIndex")
        }

        // Convert to mutable lists for easier manipulation
        val mutableSourceIssues = sourceIssues.toMutableList()
        val mutableTargetIssues = targetIssues.toMutableList()

        // Remove the issue from the source list
        val issueToMove = mutableSourceIssues.removeAt(sourceIndex)
        // Update the issue's listId to the target list
        issueToMove.listId = targetListId

        // Insert the issue into the target list at the target index
        mutableTargetIssues.add(targetIndex, issueToMove)

        // Reassign displayOrders sequentially in the source list (1, 2, 3, ...)
        for ((index, issue) in mutableSourceIssues.withIndex()) {
            issue.displayOrder = index + 1
        }

        // Reassign displayOrders sequentially in the target list (1, 2, 3, ...)
        for ((index, issue) in mutableTargetIssues.withIndex()) {
            issue.displayOrder = index + 1
        }

        // Save all issues from both lists
        repo.saveAll(mutableSourceIssues)
        repo.saveAll(mutableTargetIssues)
    }
}