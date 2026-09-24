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

    fun all(projectId: Long): List<IssueResponse> = repo.findAllByProjectIdOrderByIdDesc(projectId).map { it.toResponse() }

    fun getByListId(projectId: Long, listId: Long): List<IssueResponse> =
        repo.findByProjectIdAndListId(projectId, listId).map { it.toResponse() }

    fun get(projectId: Long, id: Long): IssueResponse =
        repo
            .findById(IssueId(projectId, id))
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Issue $id not found in project $projectId") }
            .toResponse()

    fun create(projectId: Long, req: IssueCreateRequest): IssueResponse {
        val maxId = repo.findMaxIdByProjectId(projectId)
        val nextId = if (maxId == null) 1L else maxId + 1
        val issue =
            Issue(
                projectId = projectId,
                id = nextId,
                title = req.title,
                description = req.description,
                status = req.status,
                listId = if (req.listId != null && req.listId > 0) req.listId else null,
            )
        val saved = repo.save(issue)
        eventPublisher.publishEvent(IssueSavedEvent(saved))
        return saved.toResponse()
    }

    @Transactional
    fun delete(projectId: Long, id: Long) {
        val issueId = IssueId(projectId, id)
        if (!repo.existsById(issueId)) {
            throw NoSuchElementException("Issue $id not found in project $projectId")
        }

        // Load the entity for the event (optional, but we can pass it)
        val issueToDelete =
            repo
                .findById(issueId)
                .orElseThrow { NoSuchElementException("Issue $id not found in project $projectId") }

        repo.deleteById(issueId)
        eventPublisher.publishEvent(IssueDeletedEvent(id, issueToDelete))
    }

    @Transactional
    fun patch(
        projectId: Long,
        id: Long,
        req: IssuePatchRequest,
    ): IssueResponse {
        log.info("Patch request: {}", req)

        val issueId = IssueId(projectId, id)
        val issue =
            repo
                .findById(issueId)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Issue $id not found in project $projectId") }

        issue.title = req.title ?: issue.title
        issue.description = req.description ?: issue.description
        issue.status = req.status ?: issue.status
        issue.listId = req.listId ?: issue.listId

        val saved = repo.save(issue)
        eventPublisher.publishEvent(IssueSavedEvent(saved))
        return saved.toResponse()
    }
}
