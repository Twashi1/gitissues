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

    fun all(projectId: Long): List<IssueResponse> = repo.findAllByUuid7OrderByUuid7Desc(projectId).map { it.toResponse() }

    fun getByUuid7(projectId: Long, uuid7: String): List<IssueResponse> =
        repo.findByProjectIdAndUuid7(projectId, uuid7).map { it.toResponse() }

    fun get(uuid7: String): IssueResponse {
        val issue = repo.findByUuid7(uuid7)
        if (issue == null || issue.uuid7 != uuid7) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Issue $uuid7 not found")
        }
        return issue.toResponse()
    }

    fun create(projectId: Long, req: IssueCreateRequest): IssueResponse {
        val uuid7 = req.uuid7
        val issue =
            Issue(
                uuid7 = uuid7,
                title = req.title,
                description = req.description,
                status = req.status,
                listId = if (req.listId != null && req.listId > 0) req.listId else null,
                projectId = projectId,
            )
        val saved = repo.save(issue)
        eventPublisher.publishEvent(IssueSavedEvent(saved))
        return saved.toResponse()
    }

    @Transactional
    fun delete(uuid7: String) {
        val issue = repo.findByUuid7(uuid7)
        if (issue == null || issue.uuid7 != uuid7) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Issue $uuid7 not found")
        }

        repo.delete(issue)
        eventPublisher.publishEvent(IssueDeletedEvent(uuid7, issue))
    }

    @Transactional
    fun patch(req: IssuePatchRequest): IssueResponse {
        log.info("Patch request: {}", req)

        val issue = repo.findByUuid7(req.uuid7)
        if (issue == null || issue.uuid7 != req.uuid7) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Issue ${req.uuid7} not found")
        }

        issue.title = req.title ?: issue.title
        issue.description = req.description ?: issue.description
        issue.status = req.status ?: issue.status
        issue.listId = req.listId ?: issue.listId

        val saved = repo.save(issue)
        eventPublisher.publishEvent(IssueSavedEvent(saved))
        return saved.toResponse()
    }
}