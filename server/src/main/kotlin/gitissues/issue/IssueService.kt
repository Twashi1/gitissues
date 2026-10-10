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

    fun all(projectId: Long, listId: Long? = null): List<IssueResponse> {
        val issues = if (listId != null) {
            repo.findAllByProjectIdAndListIdOrderByDisplayOrderAsc(projectId, listId)
        } else {
            repo.findAllByUuid7OrderByUuid7Desc(projectId)
        }
        return issues.map { it.toResponse() }
    }

    fun allByProjectIdAndListId(projectId: Long, listId: Long): List<IssueResponse> {
        return repo.findAllByProjectIdAndListIdOrderByDisplayOrderAsc(projectId, listId).map { it.toResponse() }
    }

    fun allByProjectId(projectId: Long): List<IssueResponse> {
        return repo.findAllByUuid7OrderByUuid7Desc(projectId).map { it.toResponse() }
    }

    fun getByUuid7(projectId: Long, listId: Long, uuid7: String): IssueResponse {
        val issue = repo.findByProjectIdAndListIdAndUuid7(projectId, listId, uuid7)?.firstOrNull() ?: run {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Issue $uuid7 not found in list $listId")
        }
        return issue.toResponse()
    }

    fun get(projectId: Long, listId: Long, uuid7: String): IssueResponse {
        val issue = repo.findByProjectIdAndListIdAndUuid7(projectId, listId, uuid7)?.firstOrNull() ?: run {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Issue $uuid7 not found")
        }
        return issue.toResponse()
    }

    fun create(projectId: Long, listId: Long, req: IssueCreateRequest): IssueResponse {
        val uuid7 = req.uuid7 ?: java.util.UUID.randomUUID().toString()
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
    fun delete(projectId: Long, listId: Long, uuid7: String) {
        val issue = repo.findByProjectIdAndListIdAndUuid7(projectId, listId, uuid7)?.firstOrNull() ?: run {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Issue $uuid7 not found in list $listId")
        }

        repo.delete(issue)
        eventPublisher.publishEvent(IssueDeletedEvent(uuid7, issue))
    }

    @Transactional
    fun patch(projectId: Long, listId: Long, req: IssuePatchRequest): IssueResponse {
        log.info("Patch request: {}", req)

        val issue = repo.findByProjectIdAndListIdAndUuid7(projectId, listId, req.uuid7)?.first() ?: run {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Issue ${req.uuid7} not found in list $listId")
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